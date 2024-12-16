package com.star.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.demo.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}