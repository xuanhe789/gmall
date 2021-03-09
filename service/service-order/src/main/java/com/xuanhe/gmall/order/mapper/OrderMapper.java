package com.xuanhe.gmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuanhe.gmall.model.order.OrderDetail;
import com.xuanhe.gmall.model.order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {

    void saveOrderDetailList(List<OrderDetail> orderDetailList);
}
