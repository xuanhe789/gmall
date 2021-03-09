package com.xuanhe.gmall.order.mapper;

import com.xuanhe.gmall.model.order.OrderDetail;
import com.xuanhe.gmall.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {
    Integer saveOrderInfo(OrderInfo orderInfo);

    void saveOrderDetailList(List<OrderDetail> orderDetailList);
}
