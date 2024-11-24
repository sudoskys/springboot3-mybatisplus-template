package com.star.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.star.demo.exception.UserNotFoundException;
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


    public User getUserByEmailAndPassword(String email, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 使用加密后的密码进行查询
        return userRepository.query().eq("email", email).eq("password", password).oneOpt().orElseThrow(() -> new UserNotFoundException("用户不存在或密码错误"));
    }

    public User getUserByEmail(String email) {
        return userRepository.query().eq("email", email).oneOpt().orElseThrow(() -> new UserNotFoundException("用户不存在"));
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

    public boolean saveUser(User user) {
        // 更新并创建用户
        return userRepository.saveOrUpdate(user);
    }

    public boolean updateUser(User user, Long userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return userRepository.update(user, queryWrapper.eq("id", userId));
    }

    public boolean deleteUserById(Long userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return userRepository.remove(queryWrapper.eq("id", userId));
    }
}