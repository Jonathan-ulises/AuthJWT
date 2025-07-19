package com.example.jwt.controller;

import com.example.jwt.dto.LoginRequest;
import com.example.jwt.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        logger.info("Login attempt by user: {} from IP: {}", request.getUsername(), httpRequest.getRemoteAddr());
        String token = "";
        try {
            token = authService.generateToken(request);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.ok(token);
    }

    @GetMapping("/welcome")
    public ResponseEntity<String> welcome(
            @RequestHeader(value = "Authorization", required = true) String authHeader,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String traceId = UUID.randomUUID().toString();

        if (authHeader == null || authHeader.isBlank()) {
            logger.warn("Unauthorized acces attempt from IP {}, UA: {}, TRACE_ID: {}", ip, userAgent, traceId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado: Falta el token");
        }

        try {
            String username = authService.
                    validateTokenAndGetUsername(authHeader.replace("Bearer ", ""));
            logger.info("Success token validation for user: {}, IP: {}, UA: {}, TRACE-ID: {}",
                    username, ip, userAgent, traceId);
            return ResponseEntity.ok("Bienvenido " + username);
        } catch (Exception e) {
            logger.warn("Invalid token attempt from IP: {}, UA: {}, TRACE-ID: {}", ip, userAgent, traceId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("TOken invalido o expirado.");
        }
    }
}
