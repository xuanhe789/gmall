package com.xuanhe.gmall.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.xuanhe.gmall"})
public class MqService8282 {
    public static void main(String[] args) {
        SpringApplication.run(MqService8282.class,args);
    }
}
