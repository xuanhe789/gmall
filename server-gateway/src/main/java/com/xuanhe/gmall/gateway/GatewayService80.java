package com.xuanhe.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.xuanhe.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages= {"com.xuanhe.gmall"})
public class GatewayService80 {
    public static void main(String[] args) {
        SpringApplication.run(GatewayService80.class,args);
    }
}
