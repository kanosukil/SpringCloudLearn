package com.cloud.learn.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VHBin
 * @date 2022/03/14 - 16:51
 */

@RestController
@RequestMapping("/hystrix")
@Slf4j
public class HystrixController {

    @GetMapping
    @HystrixCommand(fallbackMethod = "demoFallBack")
    public String demo(@RequestParam("id") Integer id) {
        log.info("id:{}", id);
        if (id <= 0) {
            throw new RuntimeException("ID is wrong!");
        }
        return "id:" + id + " Hystrix Demo is running";
    }

    public String demoFallBack(Integer id) {
        return "id:" + id + " FallBack Now";
    }
}
