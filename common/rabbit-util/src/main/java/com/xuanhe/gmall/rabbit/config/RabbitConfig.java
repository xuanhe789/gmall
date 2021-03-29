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
        return BindingBuilder.bind(payQueue()).to(delayExchange()).with(MqConst.ROUTING_PAYMENT_PAY).noargs();
    }

    @Bean
    public Queue taskQueue(){
        return new Queue(MqConst.QUEUE_TASK_1,true,false,false);
    }

    @Bean
    public DirectExchange taskExchange(){
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_TASK,true,false);
    }

    @Bean
    public Binding taskBinding(){
        return BindingBuilder.bind(taskQueue()).to(taskExchange()).with(MqConst.ROUTING_TASK_1);
    }

    @Bean
    public Queue secQueue(){
        return new Queue(MqConst.QUEUE_SECKILL_USER,true,false,false);
    }

    @Bean
    public DirectExchange secExchange(){
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_SECKILL_USER,true,false);
    }

    @Bean
    public Binding secBinding(){
        return BindingBuilder.bind(secQueue()).to(secExchange()).with(MqConst.ROUTING_SECKILL_USER);
    }

}
