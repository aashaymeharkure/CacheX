package com.aics.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetricsDTO {
    private int requestCount;
    private double lruHitRate;
    private double lfuHitRate;
    private double hybridHitRate;
    
    private double alpha;
    private double beta;
    private double entropy;
    
    private double predictionAccuracy;
    private double prefetchHitRate;
    
    private List<String> detectedPatterns;
    private String nextPrediction;
    
    private int lruEvictions;
    private int lfuEvictions;
    private int hybridEvictions;
}
