package com.cloud.learn.Controller;

import com.cloud.learn.dto.ListDTO;
import com.cloud.learn.dto.TestDTO;
import com.cloud.learn.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/03/12 - 12:27
 */

@RestController
@Slf4j
@RequestMapping("/product")
public class ProductController {

    @Value("${server.port}")
    private int port;

    @GetMapping
    public String product() {
        log.info("进入商品服务...");
        return "Product OK, the port is " + port;
    }

    @PostMapping
    public String pro() {
        log.info("product running...");
        return "Product finished, the port is " + port;
    }

    // 传参
    // queryString(?name=var)
    @GetMapping("/queryString")
    public String queryString(@RequestParam String name, @RequestParam Integer age) {
        log.info("Query:| Name: {}  Age: {}", name, age);
        return "QueryString is finished, the  port is " + port;
    }
    // 路径传参(/xxx)
    @GetMapping("/pathVariable/{id}/{name}")
    public String pathVariable(@PathVariable Integer id, @PathVariable String name) {
        log.info("Path:| ID: {} Name: {}", id, name);
        return "PathVariable is finished, the port is " + port;
    }
    // 传递对象
        // Json 形式
    @PostMapping("/objectbody")
    public String objectTransBody(@RequestBody Product product) {
        log.info("Object Body:| Product: {}", product);
        return "Object Trans is Over. The port is " + port;
    }
//        // 表单形式
//    @PostMapping("/objectpart")
//    public String objectTransPart(@RequestPart("product") Product product) {
//        log.info("Object Part:| Product: {}", product);
//        return "Object Trans is Over. The port is " + port;
//    }
    // @RequestPart 只适用于传递文件
    // 数组/集合
        // 定义接口接收数组类型参数
    @GetMapping("/array")
    public String array(String[] str) {
        for (String s : str) {
            log.info("str:{}", s);
        }
        return "Array is over. The port is " + port;
    }

    @PostMapping("/list")
    public String list(@RequestBody ListDTO list) {
        list.getList().forEach(str -> {log.info("string: {}", str);});
        return "List is Over. The port is " + port;
    }

    @GetMapping("/listGet1")
//    public String listGet(ListDTO list, TestDTO test){
    public String listGet1(ListDTO list){
        list.getList().forEach(str -> {log.info("Get|string: {}", str);});
        return "List(GET) is Over. The port is " + port;
    }
    @GetMapping("/listGet2")
    public String listGet2(ListDTO list){
        return listGet1(list);
    }
}
