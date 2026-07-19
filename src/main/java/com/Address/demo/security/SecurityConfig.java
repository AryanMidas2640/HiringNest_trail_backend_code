package com.Address.demo.security;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;

    public SecurityConfig(JwtFilter jwtFilter,
                          JwtAuthenticationEntryPoint entryPoint) {
        this.jwtFilter = jwtFilter;
        this.entryPoint = entryPoint;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                // Disable CSRF
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                // Exception handler
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(entryPoint)
                )

                // Stateless session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // ======================
                        // PUBLIC APIs
                        // ======================
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/jobs/login").permitAll()
                        .requestMatchers("/api/jobs/create-user").hasAuthority("ADMIN")                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        // ======================
                        // RECRUITER ONLY
                        // ======================
                        .requestMatchers("/api/jobs/add").permitAll()
                        .requestMatchers("/api/jobs/update/**").hasAuthority("RECRUITER")
                        .requestMatchers("/api/jobs/delete/**").hasAuthority("RECRUITER")
                        .requestMatchers("/api/jobs/my-applicants")
                        .hasAnyAuthority("RECRUITER", "ADMIN")
                        .requestMatchers("/api/jobs/active-users").hasAuthority("RECRUITER")
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        //.requestMatchers("/api/jobs/my-applicants").hasAuthority("ADMIN")
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/jobs/applications")
                        .hasAuthority("ADMIN")

                        // ======================
                        // STUDENT ONLY
                        // ======================
                        .requestMatchers("/api/jobs/apply/**").hasAuthority("STUDENT")
                        .requestMatchers("/api/jobs/my-applied").hasAuthority("STUDENT")
                        .requestMatchers("/api/student/**").hasAuthority("STUDENT")

                        // ======================
                        // BOTH
                        // ======================
                        .requestMatchers(HttpMethod.GET, "/api/jobs/all")
                        .hasAnyAuthority("STUDENT", "RECRUITER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/jobs/{jobId}")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        .requestMatchers(HttpMethod.GET, "/api/jobs/city/**")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        .requestMatchers(HttpMethod.GET, "/api/jobs/tenant/**")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        .requestMatchers("/api/resume/parse")
                        .hasAnyAuthority("STUDENT", "RECRUITER")

                        .requestMatchers("/api/jobs/send-otp").permitAll()
                        .requestMatchers("/api/jobs/verify-otp").permitAll()
                        .requestMatchers("/api/jobs/reset-password").permitAll()
                        .requestMatchers("/api/jobs/notifications")
                        .hasAnyAuthority("RECRUITER", "ADMIN")

                        .requestMatchers("/api/jobs/notifications/count")
                        .hasAnyAuthority("RECRUITER", "ADMIN")
                        // Any other request
                        .anyRequest().authenticated()
                )

                .httpBasic(httpBasic -> httpBasic.disable());

        // JWT Filter
        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }




}