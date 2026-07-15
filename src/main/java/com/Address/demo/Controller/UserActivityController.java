package com.Address.demo.Controller;

import com.Address.demo.Service.UserActivityService;
import com.Address.demo.model.UserActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService service;

    @GetMapping("/all")
    public List<UserActivity> getAllActivity() {
        return service.getAllUsers();
    }
}