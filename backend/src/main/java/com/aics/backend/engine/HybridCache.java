package com.aics.backend.engine;

import com.aics.backend.model.CacheNode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class HybridCache implements Cache {
    private final int capacity;
    private final HashMap<String, CacheNode> map;

    private double alpha = 0.5; // Weight for LFU
    private double beta = 0.5;  // Weight for LRU
    
    private LinkedList<String> recentRequests = new LinkedList<>();
    private static final int ENTROPY_WINDOW = 20;

    private int hits = 0;
    private int misses = 0;
    private int evictions = 0;

    public HybridCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    private void updateEntropyAndWeights(String key) {
        recentRequests.add(key);
        if (recentRequests.size() > ENTROPY_WINDOW) {
            recentRequests.poll();
        }
        
        // Every 10 requests, update entropy and adjust weights
        if ((hits + misses) % 10 == 0 && recentRequests.size() == ENTROPY_WINDOW) {
            HashSet<String> uniqueItems = new HashSet<>(recentRequests);
            double entropy = (double) uniqueItems.size() / ENTROPY_WINDOW;

            if (entropy > 0.75) {
                // Random traffic -> favor recency
                alpha = Math.max(alpha - 0.05, 0.1);
                beta = Math.min(beta + 0.05, 0.9);
            } else if (entropy < 0.35) {
                // Repetitive traffic -> favor frequency
                alpha = Math.min(alpha + 0.05, 0.9);
                beta = Math.max(beta - 0.05, 0.1);
            } else {
                // Mixed traffic -> balanced
                alpha = 0.5;
                beta = 0.5;
            }
        }
    }

    private double calculateEvictionScore(CacheNode node, long currentTime) {
        // frequency could be 0 for prefetched nodes
        double freqScore = node.frequency == 0 ? Double.MAX_VALUE : (1.0 / node.frequency);
        double timeSinceLastAccess = currentTime - node.lastAccessTime;
        
        return (alpha * freqScore) + (beta * timeSinceLastAccess);
    }

    private CacheNode findEvictionCandidate() {
        CacheNode target = null;
        double maxScore = -1.0;
        long currentTime = System.currentTimeMillis();

        for (CacheNode node : map.values()) {
            double score = calculateEvictionScore(node, currentTime);
            if (score > maxScore) {
                maxScore = score;
                target = node;
            }
        }
        return target;
    }

    @Override
    public String get(String key) {
        updateEntropyAndWeights(key);
        if (!map.containsKey(key)) {
            misses++;
            return null;
        }
        hits++;
        CacheNode node = map.get(key);
        node.frequency++;
        node.lastAccessTime = System.currentTimeMillis();
        return node.value;
    }

    @Override
    public void put(String key, String value) {
        if (capacity <= 0) return;
        updateEntropyAndWeights(key);

        if (map.containsKey(key)) {
            CacheNode node = map.get(key);
            node.value = value;
            node.frequency++;
            node.lastAccessTime = System.currentTimeMillis();
        } else {
            if (map.size() >= capacity) {
                CacheNode evicted = findEvictionCandidate();
                if (evicted != null) {
                    map.remove(evicted.key);
                    evictions++;
                }
            }
            CacheNode newNode = new CacheNode(key, value);
            map.put(key, newNode);
        }
    }

    @Override
    public void prefetch(String key, String value) {
        if (capacity <= 0 || map.containsKey(key)) return;

        if (map.size() >= capacity) {
            CacheNode evicted = findEvictionCandidate();
            if (evicted != null) {
                map.remove(evicted.key);
                evictions++;
            }
        }
        
        CacheNode newNode = new CacheNode(key, value);
        newNode.frequency = 0; // Ensures highest eviction score (1/frequency) initially.
        newNode.lastAccessTime = 0; // Extremely high time_since_last_access
        map.put(key, newNode);
    }

    @Override
    public int getHitCount() { return hits; }

    @Override
    public int getMissCount() { return misses; }

    @Override
    public int getEvictionCount() { return evictions; }

    @Override
    public int getSize() { return map.size(); }
    
    // Getters for frontend state tracking
    public double getAlpha() { return alpha; }
    public double getBeta() { return beta; }
    
    // Allows external entropy check 
    public double getEntropy() {
        if(recentRequests.isEmpty()) return 0;
        return (double) new HashSet<>(recentRequests).size() / recentRequests.size();
    }
}