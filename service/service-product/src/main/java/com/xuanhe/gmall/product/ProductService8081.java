package com.xuanhe.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.xuanhe.gmall")
public class ProductService8081 {
    public static void main(String[] args) {
        SpringApplication.run(ProductService8081.class,args);
    }
}
