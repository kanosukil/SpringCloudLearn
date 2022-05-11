package com.learn.rabbitmq.Controller;

import com.learn.rabbitmq.Component.MessageProducer;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rabbit")
public class RabbitController {
    @Autowired
    private MessageProducer producer;

    @GetMapping("/channel")
    public String get(@RequestParam("msg") String msg) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("content-type", "UTF-8");
        map.put("send-time", DateUtils.formatDate(new Date(), "yyyy-mm-dd-HH:mm:ss.SSS"));
        producer.sendMsgByChannel(msg, map);
        return "Successful";
    }

    @GetMapping("/source")
    public String gett(@RequestParam("msg") String msg) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("content-type", "UTF-8");
        map.put("send-time", DateUtils.formatDate(new Date(), "yyyy-mm-dd-HH:mm:ss.SSS"));
        producer.sendMsgBySource(msg, map);
        return "Successful";
    }

}
