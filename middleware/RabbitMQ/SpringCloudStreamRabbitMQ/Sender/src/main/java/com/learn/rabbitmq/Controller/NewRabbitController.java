package com.learn.rabbitmq.Controller;

import com.learn.rabbitmq.Component.NewMessageProducer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/10/9-21:14
 */
@RestController
@RequestMapping("new-rabbit")
public class NewRabbitController {
    @Resource
    private NewMessageProducer nmp;

    @GetMapping("send")
    public String send(@RequestParam("msg") String msg) {
        if (nmp.msg(msg)) {
            return "Successful";
        } else {
            return "Failure";
        }
    }
}
