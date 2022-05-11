package com.cloud.learn.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class CustomerGlobalFilter implements GlobalFilter, Ordered {

    // springmvc filter 的 request、response 被封装进入到 exchange 中
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 全局 Filter 的 操作...
        Mono<Void> filter = chain.filter(exchange);
        System.out.println(filter.toString());
        return filter;
    }

    // order:排序 返回值:int 类型 指定 filter 的执行顺序.
    // 默认按照自然数字顺序 即: 0 先于 1
    // 特殊: -1 所有 filter 之前执行 (两个自定义 filter order 都是 -1 的,由框架内部决定;不像配置文件可以从上到下排序)
    @Override
    public int getOrder() {
        return 0;
    }
}