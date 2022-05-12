package com.learn.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author VHBin
 * @date 2022/5/10-19:16
 */

@SpringBootApplication
@EnableRedisHttpSession
public class MyBatisRedisCacheApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MyBatisRedisCacheApplication.class, args);
    }


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyBatisRedisCacheApplication.class);
    }
}
