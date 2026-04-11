package com.aics.backend.controller;

import com.aics.backend.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*") // Allow frontend access
public class SimulationController {

    @Autowired
    private SimulationService simulationService;

    @PostMapping("/start")
    public ResponseEntity<String> start() {
        simulationService.start();
        return ResponseEntity.ok("Started");
    }

    @PostMapping("/pause")
    public ResponseEntity<String> pause() {
        simulationService.pause();
        return ResponseEntity.ok("Paused");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        simulationService.reset();
        return ResponseEntity.ok("Reset");
    }

    @PostMapping("/config")
    public ResponseEntity<String> config(@RequestBody Map<String, Object> body) {
        String mode = (String) body.getOrDefault("mode", "random");
        int speed = (Integer) body.getOrDefault("speed", 10);
        int cacheSize = (Integer) body.getOrDefault("cacheSize", 5);
        String customSeq = (String) body.getOrDefault("customSequence", "");
        
        simulationService.configure(mode, speed, cacheSize, customSeq);
        return ResponseEntity.ok("Configured");
    }
}
