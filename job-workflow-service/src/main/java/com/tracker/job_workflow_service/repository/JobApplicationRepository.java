package com.tracker.job_workflow_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tracker.job_workflow_service.model.JobApplication;
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
}