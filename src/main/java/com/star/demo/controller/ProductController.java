package com.star.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.demo.model.Product;
import com.star.demo.service.ProductRepository;
import com.star.demo.service.ProductService;
import com.star.demo.common.ApiResponse;
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
        boolean success = productService.saveProduct(product);
        if (!success) {
            return ApiResponse.error("创建商品失败");
        }
        return ApiResponse.success(product);
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }
        return ApiResponse.success(product);
    }

    @GetMapping
    public ApiResponse<IPage<Product>> getAllProducts(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "100") Integer size) {
        IPage<Product> products = productRepository.selectAll(page, size);
        return ApiResponse.success(products);
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        boolean success = productService.updateProduct(product, id);
        if (!success) {
            return ApiResponse.error("更新商品失败");
        }
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProductById(@PathVariable Long id) {
        boolean success = productService.removeProductById(id);
        if (!success) {
            return ApiResponse.error("删除商品失败");
        }
        return ApiResponse.success(null);
    }
}