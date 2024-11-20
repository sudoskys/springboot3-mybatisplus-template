package com.star.demo.controller;

import com.star.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class StatusController {
    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);

    private final JwtUtil jwtUtil;

    public StatusController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        Optional<String> token = jwtUtil.extractToken(request);

        if (token.isPresent() && jwtUtil.validateToken(token.get())) {
            Long username = jwtUtil.extractUserId(token.get());
            response.put("status", "valid");
            response.put("username", String.valueOf(username));
            logger.info("JWT 有效，用户：{}", username);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "invalid");
            logger.warn("无效的 JWT");
            return ResponseEntity.ok(response);
        }
    }
}
