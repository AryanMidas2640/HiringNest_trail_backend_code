package com.Address.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "Parsed_resume_data")
public class Resume {

    @Id
    private String id;

    private String name;
    private String email;
    private String phone;
    private List<String> matchedSkills;
    private String education;
    private String experience;
    private Integer matchingCount;
    private String username;
    private String tenantId;
}