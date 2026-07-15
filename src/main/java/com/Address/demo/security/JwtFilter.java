package com.Address.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;

    public JwtFilter(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();

        return path.equals("/api/jobs/login")
                || path.equals("/api/jobs/send-otp")
                || path.equals("/api/jobs/verify-otp")
                || path.equals("/api/jobs/reset-password")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("FILTER HEADER = " + authHeader);
        System.out.println("METHOD = " + request.getMethod());
        System.out.println("URI = " + request.getRequestURI());

        // HEADER MISSING
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(401, "Token Missing");
            return;
        }

        try {

            String token = authHeader.substring(7).trim();

            System.out.println("TOKEN = " + token);

            // 🔥 NOW JWT RETURNS EMAIL (NOT USERNAME)
            String email =
                    jwtHelper.getEmailFromToken(token);

            System.out.println("EMAIL = " + email);

            String role =
                    jwtHelper.getRoleFromToken(token);

            System.out.println("ROLE = " + role);

            String tenantId =
                    jwtHelper.getTenantIdFromToken(token);

            System.out.println("TENANT ID = " + tenantId);

            TenantContext.setTenant(tenantId);

            // 🔥 AUTHENTICATION BASED ON EMAIL
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,   // ✅ FIXED (was username)
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(role)
                            )
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } finally {

            TenantContext.clear();
        }
    }
}