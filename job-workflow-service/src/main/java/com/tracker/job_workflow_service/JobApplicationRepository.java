package com.tracker.job_workflow_service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// '@Repository' tells Spring Boot that this interface is a database component.
// We extend 'JpaRepository<JobApplication, Long>' which tells Spring:
// - "Manage the database operations for the JobApplication table"
// - "The primary key ID of this table is a Long number"
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    // Spring Data JPA automatically writes standard methods for us like:
    // .save(entity) -> Inserts or updates data in MySQL
    // .findAll() -> Runs 'SELECT * FROM job_applications'
    // .findById(id) -> Finds a specific row by its unique ID
}