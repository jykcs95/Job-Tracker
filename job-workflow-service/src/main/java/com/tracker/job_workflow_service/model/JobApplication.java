package com.tracker.job_workflow_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Data // This is Lombok! It automatically builds your getters and setters in the
      // background
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String roleTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationState state;

    private String salaryRange;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Automatically sets timestamps when a record is first saved
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // Automatically updates timestamps whenever a record is edited
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}