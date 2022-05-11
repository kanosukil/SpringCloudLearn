package com.cloud.learn.controller;

import com.cloud.learn.openfeignclient.HystrixClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * @author VHBin
 * @date 2022/03/14 - 18:12
 */

@RestController
@RequestMapping("/ofh")
@Slf4j
public class DemoOFHController {

    @Autowired
    private HystrixClient hystrixClient;

    @GetMapping
    @HystrixCommand(fallbackMethod = "demoFallBack")
    public String demo() {
        String s = hystrixClient.demo(new Random().nextInt() % 20 - 10);
        log.info("The Server Hystrix`s result is {}", s);
        return "OFHController is over.";
    }

    public String demoFallBack() {
        return "The Hystrix Server is down.";
    }
}
