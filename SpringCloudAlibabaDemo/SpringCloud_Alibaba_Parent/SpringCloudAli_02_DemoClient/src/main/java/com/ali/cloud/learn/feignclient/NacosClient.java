package com.ali.cloud.learn.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author VHBin
 * @date 2022/03/19 - 17:16
 */

//@FeignClient("NACOSCLIENT")
@FeignClient(value = "NACOSCLIENT")
public interface NacosClient {

    @GetMapping("/nacos-client")
    String getPort();
}
