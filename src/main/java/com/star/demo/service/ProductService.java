package com.star.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.star.demo.model.Product;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductService {

    @Resource
    private ProductRepository productRepository;

    public boolean updateProduct(Product product, Long productId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        return productRepository.update(product, queryWrapper.eq("id", productId));
    }

    public boolean saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.getById(id);
    }

    public boolean removeProductById(Long id) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        return productRepository.remove(queryWrapper.eq("id", id));
    }

}


