package com.Address.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${MAIL_USERNAME:}")
    private String username;

    @Value("${MAIL_PASSWORD:}")
    private String password;

    // ===============================
    // BASIC MAIL
    // ===============================
    public void sendMail(String to, String subject, String body) {
        sendMail(to, subject, body, null);
    }


    // ===============================
// SEND OTP MAIL
// ===============================
    public void sendOtp(String to, String otp) {

        String subject = "CareerConnect - Password Reset OTP";

        String body =
                "Hello,\n\n"
                        + "We received a request to reset your password.\n\n"
                        + "Your OTP is : " + otp + "\n\n"
                        + "This OTP is valid for 5 minutes.\n\n"
                        + "If you did not request a password reset, please ignore this email.\n\n"
                        + "Regards,\n"
                        + "CareerConnect Team";

        sendMail(to, subject, body);
    }

    // ===============================
    // ADVANCED MAIL (SAFE VERSION)
    // ===============================
    public void sendMail(String to, String subject, String body, String replyTo) {

        try {

            // 🔥 VALIDATION (IMPORTANT FIX)
            if (to == null || to.trim().isEmpty()) {
                throw new RuntimeException("Receiver email is empty/null");
            }

            SimpleMailMessage message = new SimpleMailMessage();

           // message.setFrom(fromEmail);   // system mail (must)
            message.setTo(to.trim());     // 🔥 trim removes hidden spaces
            message.setSubject(subject);
            message.setText(body);

            // 🔥 Reply-To optional
            if (replyTo != null && !replyTo.trim().isEmpty()) {
                message.setReplyTo(replyTo.trim());
            }

            System.out.println("========== MAIL DEBUG ==========");
           // System.out.println("FROM : " + fromEmail);
            System.out.println("TO   : " + to);
            System.out.println("HOST : smtp-relay.brevo.com");
            System.out.println("===============================");

            mailSender.send(message);

            System.out.println("✅ Mail sent successfully to: " + to);

        } catch (Exception e) {

            System.out.println("❌ Mail failed:");
            e.printStackTrace();   // Full error Render logs me print hoga

            throw new RuntimeException("Mail sending failed", e);

        }
    }
}