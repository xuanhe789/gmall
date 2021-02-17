package com.xuanhe.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"com.xuanhe.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"com.xuanhe.gmall"})
public class ItemService8301 {
    public static void main(String[] args) {
        SpringApplication.run(ItemService8301.class,args);
    }
}
