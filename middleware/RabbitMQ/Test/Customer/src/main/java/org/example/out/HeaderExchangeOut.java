package org.example.out;

import org.example.configs.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class HeaderExchangeOut {
    private static final Logger log = LoggerFactory.getLogger(FanoutExchangeOut.class);

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.RABBITMQ_HEADER_QUEUE_B_NAME))
    public void getB(Message message) throws Exception {
        // 获取 Message 属性 (Headers / Args)
        MessageProperties properties = message.getMessageProperties();
        log.info("\nHeaders Exchange B-{}\n{}", RabbitMQConfig.RABBITMQ_HEADER_QUEUE_B_NAME,
                new String(message.getBody(), properties.getContentType())); // 获取传递的信息
    }

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQConfig.RABBITMQ_HEADER_QUEUE_A_NAME))
    public void getA(Message message) throws Exception {
        MessageProperties properties = message.getMessageProperties();
        log.info("\nHeaders Exchange A-{}\n{}", RabbitMQConfig.RABBITMQ_HEADER_QUEUE_A_NAME,
                new String(message.getBody(), properties.getContentType()));
    }
}
