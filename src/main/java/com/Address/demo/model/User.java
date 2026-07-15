package com.Address.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;
    private String password;
    private String role;
    private String tenantId;
    private String token;
    private String email;

    // NEW FIELDS
    private boolean online;
    private String lastLogin;
    private String lastLogout;
}