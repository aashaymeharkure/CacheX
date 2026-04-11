package com.aics.backend.engine;

public interface Cache {
    String get(String key);
    void put(String key, String value);
    void prefetch(String key, String value);
    int getHitCount();
    int getMissCount();
    int getEvictionCount();
    int getSize();
}
