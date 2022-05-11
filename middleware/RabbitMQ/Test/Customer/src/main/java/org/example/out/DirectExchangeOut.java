package org.example.out;


import org.example.configs.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.RABBITMQ_DIRECT_QUEUE_NAME))
public class DirectExchangeOut {
    private static final Logger log = LoggerFactory.getLogger(DirectExchangeOut.class);

    @RabbitHandler
    public void get(Map map) {
        log.info("\nDirect Exchange-{}\n{}", RabbitMQConfig.RABBITMQ_DIRECT_QUEUE_NAME, map);
    }
}
