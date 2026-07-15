package com.Address.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "jobs")
@JsonIgnoreProperties(ignoreUnknown = true) // ✅ FIXED
public class Model {

    @Id
    private String id;

    @Indexed(unique = true)
    private String jobId;

    private String tenantId;

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Job type is required")
    private String jobType;

    private String workMode;

    private int minExperience;
    private int maxExperience;

    private double salary;

    private String description;

    // ✅ IMPORTANT FOR EMAIL SYSTEM
    @Email(message = "Invalid email format")
    private String email;

    // ===============================
    // CONSTRUCTOR
    // ===============================
    public Model() {}

    public Model(String jobTitle, String companyName, String city, String jobType,
                 String workMode, int minExperience, int maxExperience,
                 double salary, String description, String jobId,
                 String tenantId, String email) {

        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.city = city;
        this.jobType = jobType;
        this.workMode = workMode;
        this.minExperience = minExperience;
        this.maxExperience = maxExperience;
        this.salary = salary;
        this.description = description;
        this.jobId = jobId;
        this.tenantId = tenantId;
        this.email = email;
    }
}