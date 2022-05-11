package com.cloud.learn.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
//                .route("openfeignhystrix_router", r -> r.path("/ofh/**").uri("http://localhost:8821"))
                .route("openfeignhystrix_router", r -> r.path("/ofh/**").uri("lb://OPENFEIGNHYSTRIX"))
//                .route("hystrix_router", r -> r.path("/hystrix/**").uri("http://localhost:8820"))
//                .route("hystrix_router", r -> r.path("/hystrix/**").uri("lb://HYSTRIX"))
                .build();
    }
}