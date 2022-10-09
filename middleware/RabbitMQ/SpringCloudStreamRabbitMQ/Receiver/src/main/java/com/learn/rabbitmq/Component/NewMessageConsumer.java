package com.learn.rabbitmq.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/**
 * @author VHBin
 * @date 2022/10/9-21:08
 */
@Component
public class NewMessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(NewMessageConsumer.class);

    @Bean
    public Consumer<Flux<Message<String>>> msg() {
        return flux -> flux.map(message -> {
            logger.info("Message: {}", message);
            return message;
        }).subscribe();
    }
}
