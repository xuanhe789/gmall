package com.xuanhe.gmall.rabbit.config;

import com.xuanhe.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {
    //设置延迟队列交换机
    @Bean
    public CustomExchange delayExchange(){
        Map<String,Object> map=new HashMap<>();
        map.put("x-delayed-type", "direct");
        return new CustomExchange(MqConst.ROUTING_ORDER_CANCEL, "x-delayed-message", true, false, map);
    }

    @Bean
    public Queue delayQueue() {
    // 第一个参数是创建的queue的名字，第二个参数是是否支持持久化
        return new Queue(MqConst.QUEUE_ORDER_CANCEL, true);
    }
    @Bean
    public Binding bindingDelay() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(MqConst.ROUTING_ORDER_CANCEL).noargs();
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_GOODS,true,false);
    }

    @Bean
    public Queue lowerQueue(){
        return new Queue(MqConst.QUEUE_GOODS_LOWER,true,false,false);
    }

    @Bean
    public Queue upperQueue(){
        return new Queue(MqConst.QUEUE_GOODS_UPPER,true,false,false);
    }

    @Bean
    public Binding bindingLower(){
        return BindingBuilder.bind(lowerQueue()).to(directExchange()).with(MqConst.ROUTING_GOODS_LOWER);
    }

    @Bean
    public Binding bindingUpper(){
        return BindingBuilder.bind(upperQueue()).to(directExchange()).with(MqConst.ROUTING_GOODS_UPPER);
    }

    @Bean
    public DirectExchange paymentExchange(){
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY,true,false);
    }

    @Bean
    public Queue payQueue(){
        return new Queue(MqConst.QUEUE_PAYMENT_PAY,true,false,false);
    }

    @Bean
    public Binding payment(){
        return BindingBuilder.bind(payQueue()).to(paymentExchange()).with(MqConst.ROUTING_PAYMENT_PAY);
    }
}
