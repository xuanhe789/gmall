package com.xuanhe.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.xuanhe.gmall")
@EnableDiscoveryClient
@EnableFeignClients
public class ProductService8081 {
    public static void main(String[] args) {
        SpringApplication.run(ProductService8081.class,args);
    }
}
