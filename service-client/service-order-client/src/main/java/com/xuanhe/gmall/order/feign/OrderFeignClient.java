package com.xuanhe.gmall.order.feign;

import com.xuanhe.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@FeignClient(value = "service-order")
public interface OrderFeignClient {
    @GetMapping("/api/order/auth/trade")
    Result<Map<String, Object>> trade();
}
