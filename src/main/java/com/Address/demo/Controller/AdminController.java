package com.Address.demo.Controller;

import com.Address.demo.model.Notification;
import com.Address.demo.model.User;
import com.Address.demo.repositry.ApplicationRepository;
import com.Address.demo.repositry.JobRepository;
import com.Address.demo.repositry.NotificationRepository;
import com.Address.demo.repositry.UserRepositry;
import org.springframework.web.bind.annotation.*;

import com.Address.demo.dto.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.Address.demo.security.JwtHelper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepositry userRepo;
    private final JobRepository jobRepo;
    private final ApplicationRepository appRepo;
    private final NotificationRepository notificationRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;

    public AdminController(
            UserRepositry userRepo,
            JobRepository jobRepo,
            ApplicationRepository appRepo,
            PasswordEncoder passwordEncoder,
            JwtHelper jwtHelper,  NotificationRepository notificationRepository1) {

        this.userRepo = userRepo;
        this.jobRepo = jobRepo;
        this.appRepo = appRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
        this.notificationRepository = notificationRepository1;

    }

    @GetMapping("/student-count")
    public long studentCount() {
        return userRepo.countByRole("STUDENT");
    }

    @GetMapping("/recruiter-count")
    public long recruiterCount() {
        return userRepo.countByRole("RECRUITER");
    }

    @GetMapping("/notifications")
    public List<Notification> notifications() {

        return notificationRepository.findAll();
    }

    @GetMapping("/job-count")
    public long jobCount() {
        return jobRepo.count();
    }

    @GetMapping("/application-count")
    public long applicationCount() {
        return appRepo.count();
    }

    @GetMapping("/students")
    public List<User> students() {
        return userRepo.findByRole("STUDENT");
    }

    @GetMapping("/recruiters")
    public List<User> recruiters() {
        return userRepo.findByRole("RECRUITER");
    }

    @GetMapping("/dashboard")
    public Map<String,Object> dashboard() {

        return Map.of(
                "students", userRepo.countByRole("STUDENT"),
                "recruiters", userRepo.countByRole("RECRUITER"),
                "jobs", jobRepo.count(),
                "applications", appRepo.count()
        );
    }
    @PutMapping("/change-password")
    public String changePassword(
            @RequestBody ChangePasswordRequest req) {

        try {

            System.out.println("USERNAME = " + req.getUsername());
            System.out.println("OLD PASS = " + req.getCurrentPassword());
            System.out.println("NEW PASS = " + req.getNewPassword());

            User user =
                    userRepo.findByUsername(
                            req.getUsername()
                    ).orElse(null);

            System.out.println("USER FOUND = " + (user != null));

            if(user == null){
                return "User Not Found";
            }

            System.out.println("DB PASSWORD = " + user.getPassword());

            // BCrypt password check
            if(!passwordEncoder.matches(
                    req.getCurrentPassword(),
                    user.getPassword()
            )){
                return "Current Password Incorrect";
            }

// Save encoded new password
            user.setPassword(
                    passwordEncoder.encode(
                            req.getNewPassword()
                    )
            );

            userRepo.save(user);

            System.out.println("PASSWORD UPDATED");

            return "Password Updated Successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed";
        }
    }
}