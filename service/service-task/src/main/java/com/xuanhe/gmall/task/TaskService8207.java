package com.xuanhe.gmall.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan({"com.xuanhe.gmall"})
@EnableDiscoveryClient
public class TaskService8207 {
    public static void main(String[] args) {
        SpringApplication.run(TaskService8207.class,args);
    }
}
