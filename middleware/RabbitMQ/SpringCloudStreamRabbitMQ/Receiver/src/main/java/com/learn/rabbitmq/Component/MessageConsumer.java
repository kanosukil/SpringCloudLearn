package com.learn.rabbitmq.Component;

import com.learn.rabbitmq.Config.MQMessageSource;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

@EnableBinding(MQMessageSource.class)
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @StreamListener(MQMessageSource.IN_PUT)
    public void messageInput(Message msg) throws Exception {
        Channel channel = (Channel) msg.getHeaders().get(AmqpHeaders.CHANNEL);
        Long deliverTag = (Long) msg.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
//        logger.info("Msg: {}", msg);
//        logger.info("Values: {}", msg.getHeaders().values()); // 即将 message 去 key 输出
        logger.info("UUID: {}", msg.getHeaders().getId());
        logger.info("TimeStamp: {}", msg.getHeaders().getTimestamp());
        logger.info("Size: {}", msg.getHeaders().size());
        logger.info("Payload: {}", msg.getPayload()); // 获取消息
        assert channel != null;
        channel.basicAck(deliverTag, false);
    }
}
