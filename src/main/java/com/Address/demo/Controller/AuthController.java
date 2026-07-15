package com.Address.demo.Controller;

import com.Address.demo.dto.Signingrequest;
import com.Address.demo.exception.ApiException;
import com.Address.demo.model.User;
import com.Address.demo.model.UserActivity;
import com.Address.demo.repositry.UserActivityRepository;
import com.Address.demo.repositry.UserRepositry;
import com.Address.demo.security.JwtHelper;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.Address.demo.Service.EmailService;
import com.Address.demo.dto.ForgotPasswordRequest;
import com.Address.demo.dto.OtpVerifyRequest;
import com.Address.demo.dto.ResetPasswordRequest;
import com.Address.demo.model.Otp;
import com.Address.demo.repositry.OtpRepository;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api/jobs")
public class AuthController {

    private final JwtHelper jwtHelper;
    private final UserRepositry userRepositry;
    private final UserActivityRepository activityRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    public AuthController(
            JwtHelper jwtHelper,
            UserRepositry userRepositry,
            UserActivityRepository activityRepo,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            OtpRepository otpRepository) {

        this.jwtHelper = jwtHelper;
        this.userRepositry = userRepositry;
        this.activityRepo = activityRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpRepository = otpRepository;
    }

    // ==========================
    // SIGNUP
    // ==========================
    // ==========================
// CREATE USER (ADMIN)
// ==========================
    @PostMapping("/send-otp")
    public Map<String,Object> sendOtp(
            @RequestBody ForgotPasswordRequest request){

        User user =
                userRepositry.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new ApiException("Email not registered"));

        String otp =
                String.valueOf(
                        100000 + new Random().nextInt(900000)
                );

        Otp otpData =
                otpRepository.findByEmail(request.getEmail())
                        .orElse(new Otp());

        otpData.setEmail(request.getEmail());
        otpData.setOtp(otp);
        otpData.setVerified(false);
        otpData.setExpiryTime(
                LocalDateTime.now().plusMinutes(5)
        );

        otpRepository.save(otpData);

        emailService.sendOtp(
                request.getEmail(),
                otp
        );

        Map<String,Object> res =
                new HashMap<>();

        res.put("success",true);
        res.put("message","OTP Sent Successfully");

        return res;
    }
    @PostMapping("/verify-otp")
    public Map<String,Object> verifyOtp(
            @RequestBody OtpVerifyRequest request){

        Otp otp =
                otpRepository.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new ApiException("OTP not found"));

        if(LocalDateTime.now().isAfter(
                otp.getExpiryTime())){

            throw new ApiException("OTP Expired");
        }

        if(!otp.getOtp().equals(request.getOtp())){

            throw new ApiException("Invalid OTP");
        }

        otp.setVerified(true);

        otpRepository.save(otp);

        Map<String,Object> res =
                new HashMap<>();

        res.put("success",true);
        res.put("message","OTP Verified");

        return res;
    }

    @PostMapping("/reset-password")
    public Map<String,Object> resetPassword(
            @RequestBody ResetPasswordRequest request){

        Otp otp =
                otpRepository.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new ApiException("OTP verification required"));

        if(!otp.isVerified()){

            throw new ApiException("OTP not verified");
        }

        User user =
                userRepositry.findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new ApiException("User not found"));

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepositry.save(user);

        otpRepository.delete(otp);

        Map<String,Object> res =
                new HashMap<>();

        res.put("success",true);
        res.put("message","Password Reset Successfully");

        return res;
    }



    @PostMapping("/create-user")
    public Map<String, Object> createUser(@RequestBody Signingrequest request) {

        if (userRepositry.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException("User already exists");
        }

        if (!request.getRole().equalsIgnoreCase("STUDENT")
                && !request.getRole().equalsIgnoreCase("RECRUITER")) {

            throw new ApiException("Only STUDENT and RECRUITER accounts can be created.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );        user.setRole(request.getRole());

        // ✅ EMAIL SAVE (IMPORTANT)
        user.setEmail(request.getEmail());

        String tenantId = "TEN" + UUID.randomUUID()
                .toString()
                .substring(0, 5);

        user.setTenantId(tenantId);

        // 🔥 JWT SHOULD USE EMAIL (CONSISTENT SYSTEM)
        /*String token = jwtHelper.generateToken(
                user.getEmail(),   // ✅ FIXED (was username)
                user.getRole(),
                tenantId
        );

         */

       // user.setToken(token);

        user.setOnline(false);
        user.setLastLogin(null);
        user.setLastLogout(null);

        userRepositry.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User created successfully");
        //response.put("token", token);

        return response;
    }

    // ==========================
    // LOGIN
    // ==========================
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Signingrequest request) {

        User user = userRepositry.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("User not found"));

        System.out.println("LOGIN USER = " + request.getUsername());
        System.out.println("ENTERED PASS = " + request.getPassword());
        System.out.println("DB PASS = " + user.getPassword());

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new ApiException("Wrong password");
        }

        // 🔥 TOKEN NOW USES EMAIL (IMPORTANT FIX)
        String token = jwtHelper.generateToken(
                user.getEmail(),
                user.getRole(),
                user.getTenantId()
        );

        user.setToken(token);
        user.setOnline(true);
        user.setLastLogin(LocalDateTime.now().toString());
        user.setLastLogout(null);

        userRepositry.save(user);

        UserActivity old =
                activityRepo.findTopByUsernameOrderByLoginTimeDesc(
                        user.getUsername()
                );

        if (old == null || old.getLogoutTime() != null) {

            UserActivity activity = new UserActivity();
            activity.setUsername(user.getUsername());
            activity.setRole(user.getRole());
            activity.setStatus("Online");
            activity.setLoginTime(LocalDateTime.now().toString());
            activity.setLogoutTime(null);

            activityRepo.save(activity);
        }

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "Login Success");
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("tenantId", user.getTenantId());
        response.put("token", token);

        return response;
    }

    // ==========================
    // LOGOUT
    // ==========================
    @PostMapping("/logout")
    public Map<String, Object> logout(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");

        String email = jwtHelper.getEmailFromToken(token); // 🔥 FIXED

        User user = userRepositry.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        user.setOnline(false);
        user.setLastLogout(LocalDateTime.now().toString());
        user.setToken("");

        userRepositry.save(user);

        UserActivity activity =
                activityRepo.findTopByUsernameAndStatusOrderByLoginTimeDesc(
                        user.getUsername(),
                        "Online"
                );

        if (activity != null) {
            activity.setStatus("Offline");
            activity.setLogoutTime(LocalDateTime.now().toString());
            activityRepo.save(activity);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout Success");

        return response;
    }

    // ==========================
    // ACTIVE USERS
    // ==========================
    @GetMapping("/active-users")
    public Object activeUsers() {
        return activityRepo.findAll();
    }
}