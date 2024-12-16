package com.star.demo.controller;

import com.star.demo.annotation.RequireRole;
import com.star.demo.common.ApiResponse;
import com.star.demo.dto.request.CreateOrderRequest;
import com.star.demo.model.Order;
import com.star.demo.service.OrderService;
import com.star.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ApiResponse<Order> createOrder(@RequestBody CreateOrderRequest request, 
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        Order order = Order.fromRequest(request);
        order.setUserId(userId);
        return ApiResponse.success(orderService.createOrder(order));
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getOrder(@PathVariable Long id, 
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        Order order = orderService.getOrderById(id);
        if (!order.getUserId().equals(userId)) {
            return ApiResponse.error("无权访问此订单");
        }
        return ApiResponse.success(order);
    }

    @GetMapping
    @RequireRole("ADMIN")
    public ApiResponse<List<Order>> getAllOrders() {
        return ApiResponse.success(orderService.getAllOrders());
    }

    @GetMapping("/user")
    public ApiResponse<List<Order>> getCurrentUserOrders(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        return ApiResponse.success(orderService.getUserOrders(userId));
    }

    @PutMapping("/{id}")
    @RequireRole("ADMIN")
    public ApiResponse<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        return ApiResponse.success(orderService.updateOrder(order));
    }

    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public ApiResponse<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success("删除订单成功");
    }
}