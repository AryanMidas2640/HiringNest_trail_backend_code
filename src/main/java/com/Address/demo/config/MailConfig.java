package com.Address.demo.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MailConfig {

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        sender.setHost("smtp-relay.brevo.com");
        sender.setPort(465);

        sender.setUsername(username);
        sender.setPassword(password);

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");


        System.out.println("USERNAME = " + username);
        System.out.println("PASSWORD = " + password);
        System.out.println("PASSWORD LENGTH = " + password.length());

        props.put("mail.smtp.connectiontimeout","10000");
        props.put("mail.smtp.timeout","10000");
        props.put("mail.smtp.writetimeout","10000");

        return sender;
    }
}