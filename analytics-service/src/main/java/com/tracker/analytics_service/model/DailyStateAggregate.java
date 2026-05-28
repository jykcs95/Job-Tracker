package com.tracker.analytics_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "daily_state_aggregates")
@Data
public class DailyStateAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationState state;

    @Column(nullable = false)
    private Long totalCount;
}