package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.service.DirectExchangeService;
import org.example.service.FanoutExchangeService;
import org.example.service.HeaderExchangeService;
import org.example.service.TopicExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/producer/rabbitmq")
public class RabbitMQController {
    //    @Resource
    @Autowired
    private DirectExchangeService directExchangeService;
    @Resource
    private FanoutExchangeService fanoutExchangeService;
    @Resource
    private TopicExchangeService topicExchangeService;
    @Resource
    private HeaderExchangeService headerExchangeService;

    @PostMapping("/send-direct")
    public String sendDirect(@RequestParam("msg") String msg) throws Exception {
        return directExchangeService.sendMsg(msg);
    }

    @PostMapping("/send-fanout")
    public String sendFanout(@RequestParam("msg") String msg) throws Exception {
        return fanoutExchangeService.sendMsg(msg);
    }

    @PostMapping("/send-topic")
    public String sendTopic(@RequestParam("msg") String msg, @RequestParam("routing-key") String routingKey)
            throws Exception {
        return topicExchangeService.sendMsg(msg, routingKey);
    }

    @PostMapping("/send-headers")
    @SuppressWarnings("unchecked")
    public String sendHeaders(@RequestParam("msg") String msg, @RequestParam("json") String json)
            throws Exception {
        Map<String, Object> map = new ObjectMapper().readValue(json, Map.class);
        return headerExchangeService.sendMsg(msg, map);
    }

    @PostMapping("/send-request_header")
    public String sendRequestHeader(@RequestParam("msg") String msg,
                                    @RequestHeader("header_a") String headerA,
                                    @RequestHeader("header_b") String headerB)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("header_a", headerA);
        map.put("header_b", headerB);
        return headerExchangeService.sendMsg(msg, map);
    }
}
