package com.star.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.demo.mapper.OrderItemMapper;
import com.star.demo.model.OrderItem;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemRepository extends ServiceImpl<OrderItemMapper, OrderItem> {
} 