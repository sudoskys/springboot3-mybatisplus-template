package com.star.demo.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private List<OrderProduct> products;
    private String address;
    private String phone;

    @Data
    public static class OrderProduct {
        private Long productId;
        private Integer quantity;
    }
} 