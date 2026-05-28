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

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = { RequestMethod.GET })
public class AnalyticsController {

    private final DailyStateAggregateRepository repository;
    private final SseConnectionManager sseManager;

    public AnalyticsController(DailyStateAggregateRepository repository, SseConnectionManager sseManager) {
        this.repository = repository;
        this.sseManager = sseManager;
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyStateAggregate>> getDailyMetrics() {
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

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMetrics() {
        return sseManager.createConnection();
    }
}
