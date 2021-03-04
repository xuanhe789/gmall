package com.xuanhe.gmall.all.controller;

import com.xuanhe.gmall.cart.feign.CartFeignClient;
import com.xuanhe.gmall.model.product.SkuInfo;
import com.xuanhe.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CartController {
    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    ProductFeignClient productFeignClient;
    @RequestMapping("/cart.html")
    public String cartlist(){
        return "cart/index";
    }
    /**
     * 添加购物车
     * @param skuId
     * @param skuNum
     * @param request
     * @return
     */
    @RequestMapping("addCart.html")
    public String addCart(@RequestParam(name = "skuId") Long skuId,
                          @RequestParam(name = "skuNum") Integer skuNum,
                          HttpServletRequest request){
        cartFeignClient.addToCart(skuId, skuNum);

        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("skuNum",skuNum);
        return "cart/addCart";
    }


}
