package com.cloud.learn;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;

/**
 * @author VHBin
 * @date 2022/03/14 - 16:50
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
public class HystrixUsageApplication {
    public static void main(String[] args) {
        SpringApplication.run(HystrixUsageApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean getServlet() {
        ServletRegistrationBean registrationBean
                = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}
