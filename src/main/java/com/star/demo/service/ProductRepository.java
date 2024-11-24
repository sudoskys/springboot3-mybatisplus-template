package com.star.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.star.demo.mapper.ProductMapper;
import com.star.demo.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductRepository extends CrudRepository<ProductMapper, Product> {


    public IPage<Product> selectAll(int currentPage, int pageSize) {
        // 创建分页对象
        Page<Product> page = new Page<>(currentPage, pageSize);
        // 构造查询条件
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        return baseMapper.selectPage(page, queryWrapper);
    }
}
