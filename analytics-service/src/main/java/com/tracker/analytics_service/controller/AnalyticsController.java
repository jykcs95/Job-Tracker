package com.tracker.analytics_service.controller;

import com.tracker.analytics_service.model.DailyStateAggregate;
import com.tracker.analytics_service.repository.DailyStateAggregateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// '@RestController' opens up web endpoints that automatically return clean JSON data.
// '@CrossOrigin("*")' prevents CORS block errors when our React frontend reaches out.
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = { RequestMethod.GET })
public class AnalyticsController {

    private final DailyStateAggregateRepository repository;

    // Manual constructor injection to safely connect to our metrics repository
    // layer
    public AnalyticsController(DailyStateAggregateRepository repository) {
        this.repository = repository;
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
}
