package com.ali.cloud.learn.controller;

import com.ali.cloud.learn.feignclient.NacosClient;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author VHBin
 * @date 2022/03/19 - 17:13
 */

@RestController
@RequestMapping("/demo-client")
@RefreshScope
public class DemoClientController {

    private static final Logger log = LoggerFactory.getLogger(DemoClientController.class);
    @Autowired
    private NacosClient client;

    @Value("${server.port}")
    private String port;
    @Value("${username}")
    private String username;

    @GetMapping
    @SentinelResource(value = "GetPort", fallback = "getFall", blockHandler = "getBlock")
    public String getPort() {
        return "Nacos client port is " + port;
    }

    public String getFall() {
        return "Get Port fail.";
    }

    public String getBlock(BlockException ex) {
        if (ex instanceof FlowException) {
            return "流控限制";
        } else if (ex instanceof ParamFlowException) {
            return "热点参数限制";
        } else if (ex instanceof SystemBlockException) {
            return "系统限制";
        } else if (ex instanceof DegradeException) {
            return "降级限制";
        } else if (ex instanceof AuthorityException) {
            return "权限限制";
        } else {
            return "未知限制";
        }
    }

    @PostMapping
    @SentinelResource(value = "PostDemo", fallback = "postFall", blockHandler = "getBlock")
    public String demo() {
        try {
            String res = client.getPort();
            log.info("Get the info from Nacos-client: {}  user: {}", res, username);
            return "The Service is Demo Client, the port is " + port + "\n" +
                    "The invocation Service is Nacos Client." + res + "\n" +
                    "The User is " + username;
        } catch (Exception ex) {
            log.info("The Request to demo gets a exception: {}", ex.toString());
            return "The Service is Demo Client, the port is " + port + "\n" +
                    "The invocation Service which is Nacos Client gets a exception.\n" +
                    "The User is " + username;
        }
    }

    public String postFall() {
        return "业务逻辑异常.";
    }
}
