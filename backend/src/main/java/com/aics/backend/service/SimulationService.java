package com.aics.backend.service;

import com.aics.backend.engine.BenchmarkEngine;
import com.aics.backend.engine.HybridCache;
import com.aics.backend.engine.PredictionEngine;
import com.aics.backend.dto.MetricsDTO;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SimulationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private BenchmarkEngine benchmarkEngine = new BenchmarkEngine(5);
    
    private Timer timer;
    private boolean isRunning = false;
    
    // Configuration
    private String mode = "random";
    private int speed = 10; // req per sec
    private int cacheSize = 5;
    
    // Simulation state
    private Random random = new Random();
    private int patternIndex = 0;
    private String[] patternLoop = {"A", "B", "C", "A", "B", "C"};
    private String[] customSequence = {};
    private int customIndex = 0;

    public void start() {
        if (isRunning) return;
        isRunning = true;
        
        long delay = 1000 / speed;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                processNextRequest();
            }
        }, 0, delay);
    }

    public void pause() {
        if (!isRunning) return;
        isRunning = false;
        if (timer != null) {
            timer.cancel();
        }
    }

    public void reset() {
        pause();
        benchmarkEngine.reset(cacheSize);
        patternIndex = 0;
        customIndex = 0;
        broadcastMetrics();
    }

    public void configure(String mode, int speed, int cacheSize, String customSequenceStr) {
        this.mode = mode;
        this.speed = Math.max(1, speed);
        if (this.cacheSize != cacheSize) {
            this.cacheSize = cacheSize;
            benchmarkEngine.reset(this.cacheSize);
        }
        if (customSequenceStr != null && !customSequenceStr.isEmpty()) {
            this.customSequence = customSequenceStr.split(",");
            this.customIndex = 0;
        }
        if (isRunning) {
            pause();
            start(); // Restart with new speed
        }
    }

    private void processNextRequest() {
        String key = generateNextKey();
        benchmarkEngine.processRequest(key);
        broadcastMetrics();
    }

    private String generateNextKey() {
        switch (mode.toLowerCase()) {
            case "pattern":
                String pKey = patternLoop[patternIndex];
                patternIndex = (patternIndex + 1) % patternLoop.length;
                return pKey;
            case "zipfian":
                return generateZipfian();
            case "custom":
                if (customSequence.length == 0) return "A";
                String cKey = customSequence[customIndex];
                customIndex = (customIndex + 1) % customSequence.length;
                return cKey;
            case "random":
            default:
                char randChar = (char) ('A' + random.nextInt(20)); // Up to 20 unique items
                return String.valueOf(randChar);
        }
    }

    private String generateZipfian() {
        // Simple Zipfian simulation: 40% A, 20% B, 40% split among C-J
        double r = random.nextDouble();
        if (r < 0.4) return "A";
        if (r < 0.6) return "B";
        char c = (char) ('C' + random.nextInt(8));
        return String.valueOf(c);
    }

    private void broadcastMetrics() {
        HybridCache hc = benchmarkEngine.getHybridCache();
        PredictionEngine pe = benchmarkEngine.getPredictionEngine();

        MetricsDTO metrics = MetricsDTO.builder()
                .requestCount(benchmarkEngine.getRequestCount())
                .lruHitRate(benchmarkEngine.getLRUHitRate())
                .lfuHitRate(benchmarkEngine.getLFUHitRate())
                .hybridHitRate(benchmarkEngine.getHybridHitRate())
                .alpha(hc.getAlpha())
                .beta(hc.getBeta())
                .entropy(hc.getEntropy())
                .predictionAccuracy(pe.getPredictionAccuracy())
                .prefetchHitRate(pe.getPrefetchHitRate())
                .detectedPatterns(new ArrayList<>(pe.getConfirmedPatterns()))
                .nextPrediction(pe.getLastPrediction())
                .lruEvictions(benchmarkEngine.getLruCache().getEvictionCount())
                .lfuEvictions(benchmarkEngine.getLfuCache().getEvictionCount())
                .hybridEvictions(hc.getEvictionCount())
                .build();

        messagingTemplate.convertAndSend("/topic/metrics", metrics);
    }
}
