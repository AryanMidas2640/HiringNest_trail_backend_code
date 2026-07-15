// ===============================
// Application.java
// ===============================
package com.Address.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "applications")
public class Application {
    @Id
    private String id;

    private String tenantId;       // recruiter tenant
    private String studentName;
    private String studentUsername;
    private String resumeName;
    private String email;

    private String jobId;
    private String jobTitle;
    private String companyName;

    private String status; // Applied
}