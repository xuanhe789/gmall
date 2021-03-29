package com.xuanhe.gmall.task.scheduled;

import com.xuanhe.gmall.rabbit.constant.MqConst;
import com.xuanhe.gmall.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TasScheduled {
    @Autowired
    RabbitService rabbitService;
    @Scheduled(cron = "0 */2 * * * ?")
    public void cachingSecGoods(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_1,"1");
    }
}
