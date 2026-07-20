package com.Address.demo.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String username;
    private String email;
}