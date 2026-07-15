package com.Address.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    // Kis recruiter (tenant) ke liye notification hai
    private String tenantId;

    // Recruiter email (optional, agar use karna ho)
    private String recruiterEmail;

    // Student Details
    private String studentName;
    private String studentUsername;

    // Job Details
    private String jobId;
    private String jobTitle;
    private String companyName;

    // Notification Message
    private String message;

    // APPLICATION, INTERVIEW, SHORTLIST etc.
    private String type;

    // Read / Unread
    private boolean read = false;

    // Date & Time
    private String createdAt;
}