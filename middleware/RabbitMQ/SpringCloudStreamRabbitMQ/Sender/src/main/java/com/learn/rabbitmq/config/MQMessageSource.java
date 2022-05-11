package com.learn.rabbitmq.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MQMessageSource {
    String OUT_PUT = "output";

    @Output(MQMessageSource.OUT_PUT)
    MessageChannel messagechannel();
}
