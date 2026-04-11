package com.aics.backend.engine;

import com.aics.backend.model.CacheNode;
import com.aics.backend.utils.DoublyLinkedList;
import java.util.HashMap;

public class LRUCache implements Cache {
    private final int capacity;
    private final HashMap<String, CacheNode> map;
    private final DoublyLinkedList dll;
    
    private int hits = 0;
    private int misses = 0;
    private int evictions = 0;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.dll = new DoublyLinkedList();
    }

    @Override
    public String get(String key) {
        if (!map.containsKey(key)) {
            misses++;
            return null;
        }
        hits++;
        CacheNode node = map.get(key);
        node.lastAccessTime = System.currentTimeMillis();
        dll.remove(node);
        dll.addFirst(node);
        return node.value;
    }

    @Override
    public void put(String key, String value) {
        if (capacity <= 0) return;
        
        if (map.containsKey(key)) {
            CacheNode node = map.get(key);
            node.value = value;
            node.lastAccessTime = System.currentTimeMillis();
            dll.remove(node);
            dll.addFirst(node);
        } else {
            if (map.size() >= capacity) {
                CacheNode last = dll.removeLast();
                if (last != null) {
                    map.remove(last.key);
                    evictions++;
                }
            }
            CacheNode newNode = new CacheNode(key, value);
            map.put(key, newNode);
            dll.addFirst(newNode);
        }
    }

    @Override
    public void prefetch(String key, String value) {
        if (capacity <= 0 || map.containsKey(key)) return;
        
        if (map.size() >= capacity) {
            CacheNode last = dll.removeLast();
            if (last != null) {
                map.remove(last.key);
                evictions++;
            }
        }
        
        CacheNode newNode = new CacheNode(key, value);
        newNode.frequency = 0; // Indicate low priority
        map.put(key, newNode);
        
        // Add just before tail so it is evicted first
        newNode.prev = dll.tail.prev;
        newNode.next = dll.tail;
        dll.tail.prev.next = newNode;
        dll.tail.prev = newNode;
        dll.size++;
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