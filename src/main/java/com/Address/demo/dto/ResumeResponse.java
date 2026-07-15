package com.Address.demo.dto;

//package dto;

import lombok.Data;
import java.util.List;

@Data
public class ResumeResponse {

    private String name;
    private String email;
    private String phone;
    private List<String> matchedSkills;
    private String education;
    private String experience;
    private Integer matchingCount;
}