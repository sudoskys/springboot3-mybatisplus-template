package com.star.demo.controller;

import com.star.demo.model.User;
import com.star.demo.service.UserService;
import com.star.demo.common.ApiResponse;
import com.star.demo.dto.request.LoginRequest;
import com.star.demo.dto.response.AuthResponse;
import com.star.demo.dto.response.UserResponse;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody User user) {
        log.info("收到注册请求：{}", user.getEmail());
        boolean success = userService.saveUser(user);
        if (!success) {
            return ApiResponse.error("注册失败");
        }
        String token = userService.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
        return ApiResponse.success(AuthResponse.builder().token(token).user(UserResponse.fromUser(user)).build());
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        log.info("收到登录请求：{}", loginRequest.getEmail());
        User user = userService.getUserByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
        String token = userService.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
        return ApiResponse.success(AuthResponse.builder().token(token).user(UserResponse.fromUser(user)).build());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }

    @GetMapping("/user")
    public ApiResponse<UserResponse> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("/user 收到的 Authorization header: {}", token);
        if (token == null || token.isEmpty()) {
            return ApiResponse.error("未提供认证信息");
        }
        String email = userService.extractEmail(token.replace("Bearer ", ""));
        User user = userService.getUserByEmail(email);
        return ApiResponse.success(UserResponse.fromUser(user));
    }

    @PostMapping("/ping")
    public ApiResponse<AuthResponse> ping(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("/ping 收到的 Authorization header: {}", token);
        if (token == null || token.isEmpty()) {
            return ApiResponse.error("未提供认证信息");
        }
        String email = userService.extractEmail(token.replace("Bearer ", ""));
        log.info("/ping 提取的邮箱: {}", email);
        User user = userService.getUserByEmail(email);
        String newToken = userService.generateToken(email, user.getId(), user.getRole().toString());
        return ApiResponse.success(AuthResponse.builder().token(newToken).user(UserResponse.fromUser(user)).build());
    }
}