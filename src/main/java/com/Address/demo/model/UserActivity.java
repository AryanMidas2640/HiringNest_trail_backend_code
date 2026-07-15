package com.Address.demo.model;

//package com.Address.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user_activity")
public class UserActivity {

    @Id
    private String id;

    private String username;
    private String role;
    private String status;      // ACTIVE / OFFLINE
    private String loginTime;
    private String logoutTime;
}