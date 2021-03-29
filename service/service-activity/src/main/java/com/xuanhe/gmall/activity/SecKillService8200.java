package com.xuanhe.gmall.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.xuanhe.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"com.xuanhe.gmall"})
public class SecKillService8200 {
    public static void main(String[] args) {
        SpringApplication.run(SecKillService8200.class,args);
    }
}
