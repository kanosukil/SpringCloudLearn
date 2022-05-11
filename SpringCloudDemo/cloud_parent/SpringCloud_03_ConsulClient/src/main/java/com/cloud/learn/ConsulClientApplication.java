package com.cloud.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author VHBin
 * @date 2022/02/23 - 21:48
 */

@SpringBootApplication
@EnableDiscoveryClient // 非 Eureka 服务注册中心的客户端开启注解
public class ConsulClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsulClientApplication.class, args);
    }
}
