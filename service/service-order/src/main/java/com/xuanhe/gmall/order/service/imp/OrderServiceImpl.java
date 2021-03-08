package com.xuanhe.gmall.order.service.imp;

import com.xuanhe.gmall.order.mapper.OrderMapper;
import com.xuanhe.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
}
