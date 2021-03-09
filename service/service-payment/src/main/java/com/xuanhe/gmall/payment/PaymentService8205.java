package com.xuanhe.gmall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.xuanhe.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"com.xuanhe.gmall"})
public class PaymentService8205 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentService8205.class,args);
    }
}
