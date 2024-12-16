package com.star.demo.service;

import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.star.demo.mapper.OrderMapper;
import com.star.demo.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderRepository extends CrudRepository<OrderMapper, Order> {

}