package com.cloud.learn.Controller;

import com.cloud.learn.entity.Product;
import com.cloud.learn.feignclients.ProductClient;
import com.cloud.learn.vo.ListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Date;

/**
 * @author VHBin
 * @date 2022/03/12 - 12:31
 */

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Value("${server.port}")
    private int port;

    @Autowired
    private ProductClient productClient;

    @GetMapping
    public String category() {
        log.info("进入类别服务...");
        // OpenFeign
        String res = productClient.product();
        log.info("the Get is Over." + res);
        String pro = productClient.pro();
        log.info("the Post is Over" + pro);
        // 传参
        // QueryString
        String s = productClient.queryString("Kano", 17);
        log.info("QueryString is Over." + s);
        // PathVariable
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String[] strings = runtimeMXBean.getName().split("@");
        // 获取当前进程
        String s1 = productClient.pathVariable(Integer.valueOf(strings[0]), strings[1]);
        log.info("PathVariable is Over." + s1);
        // Object Trans
        String s2 = productClient.objectTransBody(new Product(101, "Client", 12.12, new Date()));
        log.info("Object Trans(Body) is Over." + s2);
//        String s3 = productClient.objectTransPart(new Product(123, "Server", 24.24, new Date()));
//        log.info("Object Trans(Part) is Over." + s3);
        // array
        String array = productClient.array(strings);
        log.info("Arrays is Over." + array);
        String list = productClient.list(new ListVO(Arrays.asList(strings)));
        log.info("List is Over." + list);
        String li = productClient.listGet1(new ListVO(Arrays.asList(strings)));
        log.info("List(GET1) is Over." + li);
//        String li = productClient.listGet(new ListVO(Arrays.asList(strings)), new TestVO(123, "BBB"));
        String li2 = productClient.listGet2(strings);
        log.info("List(GET2) is Over." + li2);
        return "Category OK, the port is " + port;
    }
}
