package com.hostel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Hostel Management System.
 * 
 * Spring Boot uses the Singleton pattern internally for all beans
 * managed by the ApplicationContext (IoC Container).
 */
@SpringBootApplication
public class HostelManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostelManagementApplication.class, args);
    }
}
