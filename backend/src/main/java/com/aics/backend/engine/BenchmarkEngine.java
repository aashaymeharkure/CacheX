package com.aics.backend.engine;

import lombok.Getter;

public class BenchmarkEngine {

    @Getter
    private LRUCache lruCache;
    @Getter
    private LFUCache lfuCache;
    @Getter
    private HybridCache hybridCache;
    @Getter
    private PredictionEngine predictionEngine;

    private int requestCount = 0;

    public BenchmarkEngine(int capacity) {
        lruCache = new LRUCache(capacity);
        lfuCache = new LFUCache(capacity);
        hybridCache = new HybridCache(capacity);
        predictionEngine = new PredictionEngine();
    }

    public void processRequest(String key) {
        requestCount++;

        // 1. Send to standalone caches for benchmarking (Misses/Hits updated internally)
        lruCache.get(key);
        if (lruCache.get(key) == null) {
            lruCache.put(key, "data-" + key);
        }

        lfuCache.get(key);
        if (lfuCache.get(key) == null) {
            lfuCache.put(key, "data-" + key);
        }

        // 2. Hybrid core logic (simulates hitting after prediction and weight adjustments)
        String hybridVal = hybridCache.get(key);
        if(hybridVal == null) {
            hybridCache.put(key, "data-" + key);
        }

        // 3. Mark request in pattern detection and prediction engine
        predictionEngine.processRequest(key);

        // 4. If we have a confident prediction, PREFETCH it into the hybrid cache
        String predictedNext = predictionEngine.getLastPrediction();
        if (predictedNext != null) {
            hybridCache.prefetch(predictedNext, "data-" + predictedNext);
        }
    }

    public void reset(int capacity) {
        lruCache = new LRUCache(capacity);
        lfuCache = new LFUCache(capacity);
        hybridCache = new HybridCache(capacity);
        predictionEngine = new PredictionEngine();
        requestCount = 0;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public double getLRUHitRate() {
        return requestCount == 0 ? 0 : (double) lruCache.getHitCount() / requestCount;
    }

    public double getLFUHitRate() {
        return requestCount == 0 ? 0 : (double) lfuCache.getHitCount() / requestCount;
    }

    public double getHybridHitRate() {
        return requestCount == 0 ? 0 : (double) hybridCache.getHitCount() / requestCount;
    }
}
