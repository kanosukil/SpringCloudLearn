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
public class FanoutExchangeOut {
    private static final Logger log = LoggerFactory.getLogger(FanoutExchangeOut.class);

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.RABBITMQ_FANOUT_QUEUE_A_NAME))
    @RabbitHandler
    public void getA(Map map) {
        log.info("\nFanout Exchange A-{}\n{}", RabbitMQConfig.RABBITMQ_FANOUT_QUEUE_A_NAME, map);
    }

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.RABBITMQ_FANOUT_QUEUE_B_NAME))
    @RabbitHandler
    public void getB(Map map) {
        log.info("\nFanout Exchange B-{}\n{}", RabbitMQConfig.RABBITMQ_FANOUT_QUEUE_B_NAME, map);
    }
}
