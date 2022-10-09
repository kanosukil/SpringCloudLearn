package com.learn.rabbitmq.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author VHBin
 * @date 2022/10/9-20:58
 */
@Component
public class NewMessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(NewMessageProducer.class);
    @Resource
    private StreamBridge streamBridge;

    public boolean msg(String msg) {
        try {
            MessageHeaders header = new MessageHeaders(new HashMap<>(1, 1f) {{
                put("content-type", "UTF-8");
            }});
            if (streamBridge.send("msg-out-0",
                    MessageBuilder.createMessage(new ObjectMapper().writeValueAsString(msg), header))) {
                logger.info("Message Sent Successfully!");
                return true;
            } else {
                logger.warn("Message Sent Fail!");
                return false;
            }
        } catch (JsonProcessingException json) {
            logger.warn("Message Sent Exception", json);
            return false;
        }

    }
}
