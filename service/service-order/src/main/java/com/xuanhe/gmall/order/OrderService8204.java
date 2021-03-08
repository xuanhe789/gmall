package com.xuanhe.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.xuanhe.gmall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.xuanhe.gmall")
public class OrderService8204 {
    public static void main(String[] args) {
        SpringApplication.run(OrderService8204.class,args);
    }
}
