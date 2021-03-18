package com.xuanhe.gmall.order.rabbitConsumer;

import com.rabbitmq.client.Channel;
import com.xuanhe.gmall.model.enums.ProcessStatus;
import com.xuanhe.gmall.model.order.OrderInfo;
import com.xuanhe.gmall.order.service.OrderService;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderConsumer {
    @Autowired
    OrderService orderService;
    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void orderCancel(Long orderId, Message message, Channel channel) throws IOException {
        try {
            if (null != orderId) {
                //防止重复消费
                OrderInfo orderInfo = orderService.getById(orderId);
                if (null != orderInfo&& orderInfo.getOrderStatus().equals(ProcessStatus.UNPAID.getOrderStatus().name())) {
                    orderService.deleteOrder(orderId);
                }
            }
            //第二个参数，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            //
            //            e.printStackTrace();第二个参数表示是否允许重复入列
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
