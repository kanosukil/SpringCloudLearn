package com.cloud.learn.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VHBin
 * @date 2022/03/16 - 11:04
 */

@RestController
@RequestMapping("/config_client")
@RefreshScope
public class DemoController {

    @Value("${label}")
    private String label;

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    @GetMapping
    public String demo() {
        log.info("ConfigClient is running.The info: {}", label);
        return "The ConfigClient is over." + label;
    }
}
