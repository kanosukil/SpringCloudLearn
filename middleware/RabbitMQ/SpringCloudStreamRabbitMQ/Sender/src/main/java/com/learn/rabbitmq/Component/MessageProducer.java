package com.learn.rabbitmq.Component;

import com.learn.rabbitmq.config.MQMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableBinding(MQMessageSource.class)
public class MessageProducer {
    @Autowired
    @Output(MQMessageSource.OUT_PUT)
    private MessageChannel channel;

    @Autowired
    private MQMessageSource source;

    public String sendMsgByChannel(String msg, Map<String, Object> properties) throws Exception {
        try {
            MessageHeaders header = new MessageHeaders(properties);
            Message message = MessageBuilder.createMessage(msg, header);
            if (channel.send(message))
                System.out.println("Successful");
            else
                System.out.println("Fail");
            return "Well";
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "Fault";
        }
    }

    public String sendMsgBySource(String msg, Map<String, Object> properties) throws Exception {
        try {
            MessageHeaders header = new MessageHeaders(properties);
            Message message = MessageBuilder.createMessage(msg, header);
            if (source.messagechannel().send(message))
                System.out.println("Successful");
            else
                System.out.println("Fail");
            return "Well";
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "Fault";
        }
    }
}
