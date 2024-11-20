package com.star.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.demo.model.Product;
import com.star.demo.service.ProductRepository;
import com.star.demo.service.ProductService;
import com.star.demo.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductRepository productRepository;

    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody Product product) {
        boolean success = productRepository.save(product);
        if (!success) {
            return ApiResponse.error("创建商品失败");
        }
        return ApiResponse.success(product);
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getProductById(@PathVariable Long id) {
        Product product = productRepository.getById(id);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        return ApiResponse.success(product);
    }

    @GetMapping
    public ApiResponse<IPage<Product>> getAllProducts(IPage<Product> pageable) {
        IPage<Product> products = productRepository.selectAll(pageable);
        return ApiResponse.success(products);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProductById(@PathVariable Long id) {
        boolean success = productRepository.removeById(id);
        if (!success) {
            return ApiResponse.error("删除商品失败");
        }
        return ApiResponse.success(null);
    }
}