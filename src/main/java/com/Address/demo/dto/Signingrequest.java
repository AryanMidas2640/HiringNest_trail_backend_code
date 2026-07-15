package com.Address.demo.dto;

import lombok.Data;

@Data
public class Signingrequest {

    private String username;
    private String password;
    private String role;
    private String email;
    //private String tenantId;

}