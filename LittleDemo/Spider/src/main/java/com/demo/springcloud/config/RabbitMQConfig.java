package com.demo.springcloud.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    // 注入 Queue
    @Bean
    public Queue rabbitmqDirectQueue() {
        return new Queue(
                RabbitMQTotalConfig.RabbitMQ_Data_Queue,
                true,
                false,
                false);
    }

    // 注入 Exchange
    @Bean
    public DirectExchange rabbitmqDirectExchange() {
        return new DirectExchange(
                RabbitMQTotalConfig.RabbitMQ_Data_DirectExchange,
                true,
                false);
    }

    // 注入 Binding
    @Bean
    public Binding bindingDirect() {
        return BindingBuilder
                .bind(rabbitmqDirectQueue())
                .to(rabbitmqDirectExchange())
                .with(RabbitMQTotalConfig.RabbitMQ_Data_Routing);
    }


}
