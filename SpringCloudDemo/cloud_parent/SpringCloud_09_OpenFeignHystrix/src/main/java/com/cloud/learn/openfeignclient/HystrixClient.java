package com.cloud.learn.openfeignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author VHBin
 * @date 2022/03/14 - 18:13
 */

@FeignClient(value = "HYSTRIX", fallback = HystrixClientFallBack.class) // fallback:当调用的服务不可用时,备选处理方式(值为 类对象)
public interface HystrixClient {
    @GetMapping("/hystrix")
    String demo(@RequestParam("id") Integer id);
}
