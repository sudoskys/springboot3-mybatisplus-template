package com.star.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.demo.model.User;

import java.util.Optional;

public interface UserMapper extends BaseMapper<User> {
    Optional<User> findByEmail(String email);
}
