package com.xuanhe.gmall.order.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuanhe.gmall.model.order.OrderDetail;
import com.xuanhe.gmall.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {

    void saveOrderDetailList(List<OrderDetail> orderDetailList);

    OrderInfo getOrderInfoById(Long orderId);

    List<OrderDetail> getOrderDetailList(Long orderId);

    List<OrderInfo> getOrderListPages(@Param("userId") String userId, @Param("page") Integer page,@Param("limit") Integer limit);
}
