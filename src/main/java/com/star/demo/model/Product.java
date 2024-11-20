package com.star.demo.model;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@Builder
@TableName("products")
public class Product {
    @NotBlank(message = "产品ID不能为空")
    private Long id;
    private String imageUrl;
    private String name;
    private String description;
    private Long uploaderId;

    @Builder.Default
    private OffsetDateTime uploadTime = OffsetDateTime.now(ZoneOffset.UTC);
    // 默认上传时间为当前时间

    private Double price;
    private Long eventId;
    private String tags;
}