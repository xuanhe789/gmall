package com.xuanhe.gmall.list.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient(value = "service-list")
public interface ListFeignClient {
}
