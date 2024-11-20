package com.star.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.star.demo.annotation.RequireRole;
import com.star.demo.model.User;
import com.star.demo.security.JwtUtil;
import com.star.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserController(BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping
    @RequireRole("ADMIN")
    public ResponseEntity<?> getAllUsers() {
        logger.info("管理员获取所有用户信息");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            logger.warn("尝试获取不存在的用户，ID：{}", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("管理员获取用户信息，用户ID：{}", id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @RequireRole("ADMIN")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        boolean success = userService.saveUser(user);
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "用户创建失败"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "用户创建成功"));
    }

    @PutMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        boolean success = userService.updateUser(User.builder().id(id).email(updates.get("email")).role(User.Role.valueOf(updates.get("role"))).password(passwordEncoder.encode(updates.get("password"))).build(), new QueryWrapper<User>().eq("id", id));
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "用户更新失败"));
        }
        return ResponseEntity.ok(Map.of("message", "用户更新成功"));
    }

    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        boolean success = userService.deleteUserById(id);
        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "用户删除失败"));
        }
        return ResponseEntity.ok(Map.of("message", "用户删除成功"));
    }
}