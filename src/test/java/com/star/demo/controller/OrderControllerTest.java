package com.star.demo.controller;

import com.star.demo.common.ApiResponse;
import com.star.demo.dto.request.CreateOrderRequest;
import com.star.demo.model.Order;
import com.star.demo.service.OrderService;
import com.star.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private OrderController orderController;

    private Order testOrder;
    private CreateOrderRequest createOrderRequest;

    private static final String TEST_TOKEN = "Bearer test-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 初始化测试数据
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(100L);
        
        createOrderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderProduct product = new CreateOrderRequest.OrderProduct();
        product.setProductId(1L);
        product.setQuantity(2);
        createOrderRequest.setProducts(List.of(product));
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        // 准备
        when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);
        when(jwtUtil.extractUserId(anyString())).thenReturn(100L);
        
        ApiResponse<Order> response = orderController.createOrder(createOrderRequest, TEST_TOKEN);
        
        // 验证
        assertNotNull(response);
        assertEquals(testOrder, response.getData());
    }

    @Test
    void getOrder_ShouldReturnOrder() {
        when(orderService.getOrderById(1L)).thenReturn(testOrder);
        when(jwtUtil.extractUserId(anyString())).thenReturn(100L);

        ApiResponse<Order> response = orderController.getOrder(1L, TEST_TOKEN);
        
        assertNotNull(response);
    }

    @Test
    void getAllOrders_ShouldReturnOrderList() {
        // 准备
        List<Order> orderList = Arrays.asList(testOrder);
        when(orderService.getAllOrders()).thenReturn(orderList);

        // 执行
        ApiResponse<List<Order>> response = orderController.getAllOrders();

        // 验证
        assertNotNull(response);
        assertEquals(orderList, response.getData());
        assertEquals(1, response.getData().size());
        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getCurrentUserOrders_ShouldReturnUserOrderList() {
        List<Order> userOrders = Arrays.asList(testOrder);
        when(orderService.getUserOrders(100L)).thenReturn(userOrders);
        when(jwtUtil.extractUserId(anyString())).thenReturn(100L);

        ApiResponse<List<Order>> response = orderController.getCurrentUserOrders(TEST_TOKEN);
        
        assertNotNull(response);
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() {
        // 准备
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        when(orderService.updateOrder(any(Order.class))).thenReturn(updatedOrder);

        // 执行
        ApiResponse<Order> response = orderController.updateOrder(1L, testOrder);

        // 验证
        assertNotNull(response);
        assertEquals(updatedOrder, response.getData());
        assertEquals(1L, response.getData().getId());
        verify(orderService, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void deleteOrder_ShouldReturnSuccessMessage() {
        // 准备
        doNothing().when(orderService).deleteOrder(1L);

        // 执行
        ApiResponse<String> response = orderController.deleteOrder(1L);

        // 验证
        assertNotNull(response);
        assertEquals("删除订单成功", response.getData());
        verify(orderService, times(1)).deleteOrder(1L);
    }
} 