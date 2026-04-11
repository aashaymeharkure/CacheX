package com.aics.backend.engine;

import java.util.*;

public class PredictionEngine {
    // Sliding Window
    private final LinkedList<String> slidingWindow = new LinkedList<>();
    private final int MAX_WINDOW_SIZE = 5;
    
    // N-gram patterns (size 3, 4, 5) -> count
    private final Map<String, Integer> patternMap = new HashMap<>();
    private final int PATTERN_THRESHOLD = 3;
    
    // Confirmed patterns
    private final Set<String> confirmedPatterns = new HashSet<>();

    // Markov Chain {item -> {next_item -> count}}
    private final Map<String, Map<String, Integer>> transitionMap = new HashMap<>();
    
    // Metrics
    private int totalPredictions = 0;
    private int correctPredictions = 0;
    private int totalPrefetches = 0;
    private int prefetchHits = 0;
    
    private String lastPrediction = null;

    public void processRequest(String currentRequest) {
        // Track accuracy if we made a prediction previously
        if (lastPrediction != null) {
            totalPredictions++;
            if (lastPrediction.equals(currentRequest)) {
                correctPredictions++;
                prefetchHits++; // Assume if it was predicted, it was prefetched and hit
            }
        }

        // Update Markov Transition Map
        if (!slidingWindow.isEmpty()) {
            String prevRequest = slidingWindow.getLast();
            transitionMap.putIfAbsent(prevRequest, new HashMap<>());
            Map<String, Integer> nextCounts = transitionMap.get(prevRequest);
            nextCounts.put(currentRequest, nextCounts.getOrDefault(currentRequest, 0) + 1);
        }

        // Update Window
        slidingWindow.add(currentRequest);
        if (slidingWindow.size() > MAX_WINDOW_SIZE) {
            slidingWindow.removeFirst();
        }

        // Extract N-Grams
        extractAndCountNGrams();
        
        // Make next prediction
        lastPrediction = predictNext(currentRequest);
    }

    private void extractAndCountNGrams() {
        int n = slidingWindow.size();
        if (n < 3) return;

        // Extract 3, 4, 5 grams ending at the newest request
        for (int size = 3; size <= Math.min(n, 5); size++) {
            StringBuilder ngramBuilder = new StringBuilder();
            for (int i = n - size; i < n; i++) {
                ngramBuilder.append(slidingWindow.get(i)).append(i == n - 1 ? "" : ",");
            }
            String ngram = ngramBuilder.toString();
            int count = patternMap.getOrDefault(ngram, 0) + 1;
            patternMap.put(ngram, count);

            if (count >= PATTERN_THRESHOLD) {
                confirmedPatterns.add(ngram);
            }
        }
    }

    public String predictNext(String currentItem) {
        if (!transitionMap.containsKey(currentItem)) return null;

        Map<String, Integer> nextCounts = transitionMap.get(currentItem);
        int totalTransitions = nextCounts.values().stream().mapToInt(Integer::intValue).sum();
        
        String bestNext = null;
        double maxProb = -1.0;
        
        for (Map.Entry<String, Integer> entry : nextCounts.entrySet()) {
            double prob = (double) entry.getValue() / totalTransitions;
            if (prob > maxProb) {
                maxProb = prob;
                bestNext = entry.getKey();
            }
        }

        // If confidence > 50%, prefetch candidate is valid
        if (maxProb > 0.5) {
            totalPrefetches++;
            return bestNext;
        }
        
        return null;
    }
    
    public double getPredictionAccuracy() {
        return totalPredictions == 0 ? 0.0 : (double) correctPredictions / totalPredictions;
    }
    
    public double getPrefetchHitRate() {
        return totalPrefetches == 0 ? 0.0 : (double) prefetchHits / totalPrefetches;
    }

    public Set<String> getConfirmedPatterns() {
        return confirmedPatterns;
    }
    
    // Expose for UI
    public String getLastPrediction() {
        return lastPrediction;
    }
}
