package com.aics.backend.engine;

import com.aics.backend.model.CacheNode;
import com.aics.backend.utils.MinHeap;
import java.util.HashMap;

public class LFUCache implements Cache {
    private final int capacity;
    private final HashMap<String, CacheNode> map;
    private final MinHeap minHeap;

    private int hits = 0;
    private int misses = 0;
    private int evictions = 0;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.minHeap = new MinHeap(capacity);
    }

    @Override
    public String get(String key) {
        if (!map.containsKey(key)) {
            misses++;
            return null;
        }
        hits++;
        CacheNode node = map.get(key);
        node.frequency++;
        node.lastAccessTime = System.currentTimeMillis();
        minHeap.update(node);
        return node.value;
    }

    @Override
    public void put(String key, String value) {
        if (capacity <= 0) return;

        if (map.containsKey(key)) {
            CacheNode node = map.get(key);
            node.value = value;
            node.frequency++;
            node.lastAccessTime = System.currentTimeMillis();
            minHeap.update(node);
        } else {
            if (map.size() >= capacity) {
                CacheNode evicted = minHeap.extractMin();
                if (evicted != null) {
                    map.remove(evicted.key);
                    evictions++;
                }
            }
            CacheNode newNode = new CacheNode(key, value);
            map.put(key, newNode);
            minHeap.add(newNode);
        }
    }

    @Override
    public void prefetch(String key, String value) {
        if (capacity <= 0 || map.containsKey(key)) return;

        if (map.size() >= capacity) {
            CacheNode evicted = minHeap.extractMin();
            if (evicted != null) {
                map.remove(evicted.key);
                evictions++;
            }
        }
        CacheNode newNode = new CacheNode(key, value);
        newNode.frequency = 0; // Ensures it's the minimum!
        newNode.lastAccessTime = 0; // Lower than others
        map.put(key, newNode);
        minHeap.add(newNode);
    }

    @Override
    public int getHitCount() { return hits; }

    @Override
    public int getMissCount() { return misses; }

    @Override
    public int getEvictionCount() { return evictions; }

    @Override
    public int getSize() { return map.size(); }
}