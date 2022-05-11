package com.learn.rabbitmq.Config;


import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MQMessageSource {
    String IN_PUT = "input";

    @Input(MQMessageSource.IN_PUT)
    SubscribableChannel SubscribableChannel();
}
