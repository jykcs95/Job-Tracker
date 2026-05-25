package com.tracker.analytics_service.controller;

import com.tracker.analytics_service.config.SseConnectionManager;
import com.tracker.analytics_service.model.DailyStateAggregate;
import com.tracker.analytics_service.repository.DailyStateAggregateRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import com.tracker.analytics_service.model.ApplicationState;

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
        // Fetch aggregated rows for today only and ensure we return the three expected
        // states
        LocalDate today = LocalDate.now();
        List<DailyStateAggregate> rows = repository.findByLogDate(today);

        List<DailyStateAggregate> result = new ArrayList<>();
        for (ApplicationState s : ApplicationState.values()) {
            DailyStateAggregate match = rows.stream().filter(r -> r.getState() == s).findFirst().orElseGet(() -> {
                DailyStateAggregate empty = new DailyStateAggregate();
                empty.setLogDate(today);
                empty.setState(s);
                empty.setTotalCount(0L);
                return empty;
            });
            result.add(match);
        }

        return ResponseEntity.ok(result);
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
