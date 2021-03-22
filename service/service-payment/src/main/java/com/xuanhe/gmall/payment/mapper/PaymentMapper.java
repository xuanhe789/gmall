package com.xuanhe.gmall.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuanhe.gmall.model.payment.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper extends BaseMapper<PaymentInfo> {
    Integer selectByOrderIdAndPaymentType(@Param("orderId") Long id,@Param("paymentType") String paymentType);

    void updateStatus(@Param("out_trade_no") String out_trade_no,@Param("status") String name);

    void closePayMent(@Param("outTradeNo") String outTradeNo,@Param("status") String status);
}
