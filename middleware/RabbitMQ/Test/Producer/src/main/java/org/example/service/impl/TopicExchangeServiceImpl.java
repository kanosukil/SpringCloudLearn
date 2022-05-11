package org.example.service.impl;

import org.example.configs.RabbitMQConfig;
import org.example.service.TopicExchangeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TopicExchangeServiceImpl implements TopicExchangeService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private RabbitTemplate template;

    @Override
    public String sendMsg(String msg, String routingKey) throws Exception {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("Msg", msg);
            map.put("Date", sdf.format(new Date()));
            template.convertAndSend(RabbitMQConfig.RABBITMQ_TOPIC_EXCHANGE_NAME, routingKey, map);
            return "OK!";
        } catch (Exception ex) {
            return "Error!";
        }
    }
}
