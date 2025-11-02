package com.eventflow.service;

import com.eventflow.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class AuthService {
    private static final String SECRET_STRING = System.getenv().getOrDefault(
            "JWT_SECRET",
            "your-very-secure-256-bit-secret-key-change-this-in-production-12345678"
    );
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(Integer.toString(user.getUserId()))
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        // Modern JJWT 0.12.x API
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
