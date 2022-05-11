package com.ali.cloud.learn.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author VHBin
 * @date 2022/03/19 - 17:14
 */

@FeignClient("DEMOCLIENT")
public interface DemoClient {

    @GetMapping("/demo-client")
    String getPort();
}
