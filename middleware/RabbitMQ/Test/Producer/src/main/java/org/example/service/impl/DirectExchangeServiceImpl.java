package org.example.service.impl;

import org.example.configs.RabbitMQConfig;
import org.example.service.DirectExchangeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Service
public class DirectExchangeServiceImpl implements DirectExchangeService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private RabbitTemplate template;

    @Override
    public String sendMsg(String msg) throws Exception {
        try {
            msg += "Hello World!" + msg;
            HashMap<String, Object> map = new HashMap();
            map.put("Date", sdf.format(new Date()));
            map.put("msg", msg);
            template.convertAndSend(
                    RabbitMQConfig.RABBITMQ_DIRECT_EXCHANGE_NAME,
                    RabbitMQConfig.RABBITMQ_DIRECT_EXCHANGE_ROUTING,
                    map);
            return "OK!";
        } catch (Exception ex) {
            return "Error!";
        }
    }
}
