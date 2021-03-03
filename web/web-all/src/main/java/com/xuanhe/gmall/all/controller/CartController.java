package com.xuanhe.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CartController {
    @RequestMapping("/cart/cart.html")
    public String cartlist(){
        return "cart/index";
    }
}
