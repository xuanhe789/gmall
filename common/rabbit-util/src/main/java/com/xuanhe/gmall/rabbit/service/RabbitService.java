package com.xuanhe.gmall.rabbit.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    //正常发送信息
    public void sendMessage(String exchange,String routingKey,Object message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }
    //发送信息至死信队列
    public void sendDeadMessage(String exchange,String routingKey,Object message,Long expireTime){
        rabbitTemplate.convertAndSend(exchange,routingKey,message,message1 -> {
            message1.getMessageProperties().setExpiration(1*1000*expireTime+"");
            return message1;
        });
    }
    //发送信息，设置消息的ttl延迟时间，使用rabbitmq延迟队列插件实现，消息不会真被发送到队列里面去等待过期
    //而用死信队列实现延迟队列是把消息真正发送到队列中，等待消息过期才会被消费
    public void sendDelayMessage(String exchange,String routingKey,Object message,Long delayTime){
        rabbitTemplate.convertAndSend(exchange,routingKey,message,message1 -> {
            message1.getMessageProperties().setDelay(Integer.parseInt(delayTime+"")*1000);
            return message1;
        });
    }
}
