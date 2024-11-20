package com.star.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.star.demo.model.User;
import com.star.demo.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.util.List;

@Slf4j
@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private JwtUtil jwtUtil;

    public boolean saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserByEmailAndPassword(String email, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return userRepository.query().eq("email", email).eq("password", password).oneOpt().orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public User getUserByEmail(String email) {
        return userRepository.query().eq("email", email).oneOpt().orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public String generateToken(String email, Long id, String role) {
        return jwtUtil.generateToken(email, id, role);
    }

    public String extractEmail(String token) {
        return jwtUtil.extractEmail(token);
    }

    public List<User> getAllUsers() {
        return userRepository.list();
    }

    public User getUserById(Long id) {
        return userRepository.getById(id);
    }

    public boolean updateUser(User user, QueryWrapper<User> queryWrapper) {
        return userRepository.update(user, queryWrapper);
    }

    public boolean deleteUserById(Long id) {
        return userRepository.removeById(id);
    }
}