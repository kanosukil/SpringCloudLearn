package com.cloud.learn.feignclients;

import com.cloud.learn.entity.Product;
import com.cloud.learn.vo.ListVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;


/**
 * @author VHBin
 * @date 2022/03/12 - 12:43
 */

// 调用服务接口
@FeignClient("PRODUCT") // value : 服务id
@RequestMapping("/product")
public interface ProductClient {

    @GetMapping
    String product();

    @PostMapping
    String pro();

    // 传参
    // QueryString
    @GetMapping("/queryString")
    String queryString(@RequestParam("name") String name, @RequestParam("age") Integer age);
    // 路径
    @GetMapping("/pathVariable/{id}/{name}")
    String pathVariable(@PathVariable("id") Integer id, @PathVariable("name") String name);
    // 传递对象
    @PostMapping("/objectbody")
    String objectTransBody(@RequestBody Product product); // @RequestBody Json格式传递参数
//    @PostMapping("/objectpart")
//    String objectTransPart(@RequestPart("product") Product product);
    // @RequestPart 只适用于传递文件
    // 数组\集合
        // 数组
    @GetMapping("/array")
    String array(@RequestParam("str") String[] str);
        // 列表
    @PostMapping("/list")
    String list(@RequestBody ListVO list);
    @GetMapping("/listGet1")
    String listGet1(@SpringQueryMap ListVO list);
    //    String listGet(@SpringQueryMap ListVO list, @SpringQueryMap TestVO test); // TestVO 对象的数据没有传到接收方
    @GetMapping("/listGet2")
    String listGet2(@RequestParam("list") String[] str);
    // 原因: 其组成的路径最终结果符合接收方的接收方法
}
