package com.star.demo.model;

import com.baomidou.mybatisplus.annotation.*;
import com.star.demo.dto.request.CreateOrderRequest;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @TableField(exist = false)
    private List<OrderItem> items;
    
    public static Order fromRequest(CreateOrderRequest request) {
        Order order = new Order();
        order.setItems(request.getProducts().stream()
            .map(p -> OrderItem.builder()
                .productId(p.getProductId())
                .quantity(p.getQuantity())
                .build())
            .collect(Collectors.toList()));
        return order;
    }
}
