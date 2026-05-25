package com.tracker.analytics_service.controller;

import com.tracker.analytics_service.config.SseConnectionManager;
import com.tracker.analytics_service.model.DailyStateAggregate;
import com.tracker.analytics_service.repository.DailyStateAggregateRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

// '@RestController' opens up web endpoints that automatically return clean JSON data.
// '@CrossOrigin("*")' prevents CORS block errors when our React frontend reaches out.
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = { RequestMethod.GET })
public class AnalyticsController {

    private final DailyStateAggregateRepository repository;
    private final SseConnectionManager sseManager;

    // Manual constructor injection to safely connect to our metrics repository
    // layer
    public AnalyticsController(DailyStateAggregateRepository repository, SseConnectionManager sseManager) {
        this.repository = repository;
        this.sseManager = sseManager;
    }

    /**
     * Endpoint: Fetch all daily aggregate counts for the chart
     * URL: GET http://localhost:8081/api/analytics/daily
     */
    @GetMapping("/daily")
    public ResponseEntity<List<DailyStateAggregate>> getDailyMetrics() {
        // Fetch all aggregated counting rows sitting inside MySQL
        List<DailyStateAggregate> metrics = repository.findAll();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Endpoint: Open a permanent stream channel to the browser
     * URL: GET http://localhost:8081/api/analytics/stream
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMetrics() {
        return sseManager.createConnection();
    }
}
