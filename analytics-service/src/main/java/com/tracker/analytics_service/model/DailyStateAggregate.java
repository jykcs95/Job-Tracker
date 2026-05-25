package com.tracker.analytics_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

// '@Entity' instructs Spring to turn this class into a tracking table in MySQL.
@Entity
@Table(name = "daily_state_aggregates")
@Data // Lombok automatically writes getters, setters, and toString strings behind the
      // scenes.
public class DailyStateAggregate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // THe specific day we are logging total system counts for
    @Column(nullable = false)
    private LocalDate logDate;

    // The state we are summarizing (APPLIED, INTERVIEWING, or DONE).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationState state;

    // The running total calculation of how many jobs sit in this state system-wide.
    @Column(nullable = false)
    private Long totalCount;
}