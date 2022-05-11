package com.cloud.learn.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VHBin
 * @date 2022/03/08 - 15:06
 */

@RestController
@RequestMapping("order")
@Slf4j
public class OrderController {
    @Value("${server.port}")
    private int port;
    @GetMapping
    public String demo() {
        log.info("order demo running...");
        return "Port:" + port + ".Order Demo Return.";
    }
}
