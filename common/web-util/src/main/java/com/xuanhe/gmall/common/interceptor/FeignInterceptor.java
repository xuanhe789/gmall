package com.xuanhe.gmall.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class FeignInterceptor implements RequestInterceptor {
//先访问的weball，weball再通过feign调用服务，防止请求头信息丢失，得加上这个
//    1，我们在web-cart微服务中通过cartFeignClient.addToCart(skuId, skuNum)添加购物车，调用service-cart微服务
//    2，如果不添加Feign拦截器，service-cart微服务addToCart获取不到用户信息
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        requestTemplate.header("userTempId", request.getHeader("userTempId"));
        requestTemplate.header("userId", request.getHeader("userId"));
    }
}
