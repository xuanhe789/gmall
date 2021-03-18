package com.xuanhe.gmall.list.consumer;

import com.rabbitmq.client.Channel;
import com.xuanhe.gmall.list.repository.GoodsRespository;
import com.xuanhe.gmall.model.list.Goods;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import com.xuanhe.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoodsConsumer {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    GoodsRespository goodsRespository;
    @RabbitListener(queues = MqConst.QUEUE_GOODS_UPPER)
    public void upper(Long skuId, Message message,Channel channel) throws IOException {
        try {
            Goods goods = productFeignClient.getGoodsBySkuId(skuId);
            if (goods!=null){
                goodsRespository.save(goods);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitListener(queues = MqConst.QUEUE_GOODS_LOWER)
    public void lower(Long skuId, Message message,Channel channel) throws IOException {
        try {
            goodsRespository.deleteById(skuId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
