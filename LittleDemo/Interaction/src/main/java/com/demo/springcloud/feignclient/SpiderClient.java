package com.demo.springcloud.feignclient;

import com.demo.springcloud.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("SpiderForData")
public interface SpiderClient {
    @GetMapping("/spider/get")
    ResultVO search(@RequestParam("search") String search);
}
