package com.example.jwt.service;

import com.example.jwt.dto.LoginRequest;
import com.example.jwt.repositories.UsersRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
@AllArgsConstructor
public class AuthService {
    private static final String SECRET = "12345678901234567890123456789012";

    private UsersRepository usersRepository;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(LoginRequest request) throws Exception {

        var result = usersRepository.findByUsername(request.getUsername());
        if (result.isPresent()) {
            return Jwts.builder()
                    .setSubject(request.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // 1 day
//                .setExpiration(new Date(System.currentTimeMillis() + 60000)) // 1 minute
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } else {
            throw new Exception("NO existe usuario");
        }

    }

    public String validateTokenAndGetUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
