package org.example.service.impl;

import org.example.configs.RabbitMQConfig;
import org.example.service.HeaderExchangeService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class HeaderExchangeServiceImpl implements HeaderExchangeService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Resource
    private RabbitTemplate template;

    @Override
    public String sendMsg(String msg, Map<String, Object> map) throws Exception {
        try {
            MessageProperties properties = new MessageProperties();
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            properties.setContentType("UTF-8");
            properties.getHeaders().putAll(map);
            Message message = new Message(msg.getBytes(StandardCharsets.UTF_8), properties);
            template.convertAndSend(
                    RabbitMQConfig.RABBITMQ_HEADER_EXCHANGE_NAME,
                    null, message);
            return "OK!";
        } catch (Exception ex) {
            return "Error!";
        }
    }
}
