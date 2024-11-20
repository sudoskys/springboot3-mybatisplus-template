package com.star.demo.service;

import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.star.demo.mapper.UserMapper;
import com.star.demo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserRepository extends CrudRepository<UserMapper, User> {

}