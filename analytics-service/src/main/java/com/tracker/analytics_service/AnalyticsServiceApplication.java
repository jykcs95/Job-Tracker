package com.tracker.analytics_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// '@SpringBootApplication' is a powerful macro tag. It tells Spring Boot to turn on
// automatic configuration, component scanning, and property loading for this microservice.
@SpringBootApplication
public class AnalyticsServiceApplication {

	// The 'main' method is the absolute entry point for Java.
	// When you run 'mvnw.cmd spring-boot:run', Maven searches specifically for this
	// block.
	public static void main(String[] args) {
		// This launches the Spring Boot runtime environment for our Analytics Engine.
		SpringApplication.run(AnalyticsServiceApplication.class, args);
	}
}
