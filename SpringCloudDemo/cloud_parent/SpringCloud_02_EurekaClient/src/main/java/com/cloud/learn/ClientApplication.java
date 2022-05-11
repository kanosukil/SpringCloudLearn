package com.cloud.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author VHBin
 * @date 2022/02/22 - 12:31
 */

@SpringBootApplication
@EnableEurekaClient // 让当前服务作为 Eureka Server 的客户端 注册服务
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
