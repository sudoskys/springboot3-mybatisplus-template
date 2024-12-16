package com.star.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.demo.common.ErrorCode;
import com.star.demo.exception.BusinessException;
import com.star.demo.model.Order;
import com.star.demo.model.OrderItem;
import com.star.demo.model.Product;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private ProductService productService;

    @Resource
    private OrderItemRepository orderItemRepository;

    @Transactional
    public Order createOrder(Order order) {
        double totalPrice = 0.0;
        for (OrderItem item : order.getItems()) {
            Product product = productService.getProductById(item.getProductId());
            item.setPrice(product.getPrice());
            totalPrice += product.getPrice() * item.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        
        try {
            orderRepository.save(order);
            for (OrderItem item : order.getItems()) {
                item.setOrderId(order.getId());
                orderItemRepository.save(item);
            }
            return order;
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建订单失败");
        }
    }

    private List<OrderItem> getOrderItems(Long orderId) {
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return orderItemRepository.list(queryWrapper);
    }

    private Order loadOrderItems(Order order) {
        if (order != null) {
            order.setItems(getOrderItems(order.getId()));
        }
        return order;
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.getById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        return loadOrderItems(order);
    }

    public List<Order> getAllOrders(int page, int size) {
        Page<Order> pageParam = new Page<>(page, size);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        return orderRepository.page(pageParam, queryWrapper).getRecords().stream()
            .map(this::loadOrderItems)
            .collect(Collectors.toList());
    }

    @Transactional
    public Order updateOrder(Order order) {
        if (!orderRepository.updateById(order)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "更新订单失败，订单可能不存在");
        }
        return order;
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.removeById(id)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "删除订单失败，订单可能不存在");
        }
    }

    public List<Order> getUserOrders(Long userId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return orderRepository.list(queryWrapper).stream()
            .map(this::loadOrderItems)
            .collect(Collectors.toList());
    }
}