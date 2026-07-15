package com.Address.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtHelper {

    private final String SECRET =
            "mysecretkeymysecretkeymysecretkey123";

    private final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    // ===============================
    // GENERATE TOKEN (EMAIL BASED 🔥)
    // ===============================
    public String generateToken(
            String email,
            String role,
            String tenantId) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("role", role);
        claims.put("tenantId", tenantId);
        claims.put("email", email); // 🔥 extra safety

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)   // ✅ NOW SUBJECT = EMAIL
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ===============================
    // GET EMAIL FROM TOKEN 🔥
    // ===============================
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject(); // now returns EMAIL
    }

    // ===============================
    // GET ROLE
    // ===============================
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ===============================
    // GET TENANT ID
    // ===============================
    public String getTenantIdFromToken(String token) {
        return getClaims(token).get("tenantId", String.class);
    }

    // ===============================
    // PRIVATE CLAIMS
    // ===============================
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}