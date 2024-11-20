package com.star.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.star.demo.mapper.ProductMapper;
import com.star.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ProductRepository extends CrudRepository<ProductMapper, Product> {


    public IPage<Product> selectAll(IPage<Product> page) {
        // baseMapper
        return baseMapper.selectPage(page, null);
    }
}
