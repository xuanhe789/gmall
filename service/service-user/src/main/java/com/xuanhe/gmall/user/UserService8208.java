package com.xuanhe.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan({"com.xuanhe.gmall"})
@EnableDiscoveryClient
public class UserService8208 {
    public static void main(String[] args) {
        SpringApplication.run(UserService8208.class,args);
    }
}
