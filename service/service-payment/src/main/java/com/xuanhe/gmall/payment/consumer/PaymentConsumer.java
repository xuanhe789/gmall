package com.xuanhe.gmall.payment.consumer;

import com.rabbitmq.client.Channel;
import com.xuanhe.gmall.order.feign.OrderFeignClient;
import com.xuanhe.gmall.payment.service.AlipayService;
import com.xuanhe.gmall.payment.service.PaymentService;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class PaymentConsumer {
    @Autowired
    PaymentService paymentService;
    @Autowired
    AlipayService alipayService;


    @RabbitListener(queues = MqConst.QUEUE_PAYMENT_PAY)
    public void validateStatus(Map<String,Object> map, Message message, Channel channel) throws IOException {
        //通过out_trade_no查询订单的支付状态
        try {
            String out_trade_no = (String) map.get("out_trade_no");
            Integer count = (Integer) map.get("count");
            long delayTime = (long) map.get("delayTime");
            if (count <= 6) {
                //获取支付订单的状态
                Map map1 = alipayService.queryStatus(out_trade_no);
                //若还是创建未支付状态，则继续查询
                if (map1 == null && map1.get("trade_status").equals("WAIT_BUYER_PAY")) {
                    System.out.println("订单未支付且查询次数未超过6");
                    paymentService.sendMessageQuery(out_trade_no, ++count, delayTime * 3);
                } else {
                    paymentService.updateStatusByOutTradeNo(out_trade_no);
                }

            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
