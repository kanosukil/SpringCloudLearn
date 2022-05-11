package com.cloud.learn.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/03/08 - 15:04
 */

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    // 服务注册和发现客户端对象
    @Autowired
    private DiscoveryClient discoveryClient;

    // 负载均衡的客户端对象
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate template;

    @GetMapping
    public String demo(){
        log.info("user demo running...");
//        // 1. 调用订单服务(地址 http://localhost:9999/order 方法:get 返回值:String)接收返回值
//        // 创建 RestTemplate 对象
//        String res = template.getForObject("http://localhost:8802/order", String.class);
//        log.info("订单服务:{}", res);
//        return "User Demo Return.\nPure RestTemplate:" + res;

//        // 2. Ribbon 组件 + RestTemplate 实现负载均衡
//        // ① DiscoveryClient
//        List<ServiceInstance> orders = discoveryClient.getInstances("ORDERS");
//        orders.forEach(serviceInstance -> {
//            log.info("host:{} port:{} uri:{}", serviceInstance.getHost(), serviceInstance.getPort(), serviceInstance.getUri());
//            log.info("InstanceID:{} ServiceID:{}", serviceInstance.getInstanceId(), serviceInstance.getServiceId());
//            log.info("MetaData:{} Scheme:{}", serviceInstance.getMetadata(), serviceInstance.getScheme());
//        });
//        return "DiscoveryClient + RestTemplate:" + template.getForObject(orders.get(0).getUri() + "/order", String.class);

//        // ② LoadBalancerClient (默认轮巡策略)
//        ServiceInstance order = loadBalancerClient.choose("ORDERS");
//        log.info("LoadBalancerClient: host:{} port:{} uri:{}", order.getHost(), order.getPort(), order.getUri());
//        return "LoadBalancerClient + RestTemplate:" + template.getForObject(order.getUri() + "/order", String.class);

        // ③ @LoadBalanced 注解 (让对象具有 ribbon 负载均衡的特性)
        return "@LoadBalance:" + template.getForObject("http://ORDERS/order", String.class);
    }
}
