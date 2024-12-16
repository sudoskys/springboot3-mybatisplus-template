package com.star.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.demo.model.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}

