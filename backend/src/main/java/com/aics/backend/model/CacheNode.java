package com.aics.backend.model;

public class CacheNode {
    public String key;
    public String value;
    public int frequency;
    public long lastAccessTime;
    public CacheNode prev;
    public CacheNode next;

    public CacheNode(String key, String value) {
        this.key = key;
        this.value = value;
        this.frequency = 1;
        this.lastAccessTime = System.currentTimeMillis();
    }
}
