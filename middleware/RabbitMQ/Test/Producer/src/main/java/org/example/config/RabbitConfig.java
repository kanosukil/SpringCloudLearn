package org.example.config;

import org.example.configs.RabbitMQConfig;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig implements BeanPostProcessor {
    @Resource
    private RabbitAdmin rabbitAdmin;

    // Direct Exchange
    @Bean
    public Queue rabbitmqDirectQueue() {
        return new Queue(RabbitMQConfig.RABBITMQ_DIRECT_QUEUE_NAME, true, false, false);
    }

    @Bean
    public DirectExchange rabbitmqDirectExchange() {
        return new DirectExchange(RabbitMQConfig.RABBITMQ_DIRECT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindDirect() {
        return BindingBuilder
                .bind(rabbitmqDirectQueue())
                .to(rabbitmqDirectExchange())
                .with(RabbitMQConfig.RABBITMQ_DIRECT_EXCHANGE_ROUTING);
    }

    // Fanout Exchange
    @Bean
    public Queue rabbitmqFanoutQueueA() {
        return new Queue(RabbitMQConfig.RABBITMQ_FANOUT_QUEUE_A_NAME, true, false, false);
    }

    @Bean
    public Queue rabbitmqFanoutQueueB() {
        return new Queue(RabbitMQConfig.RABBITMQ_FANOUT_QUEUE_B_NAME, true, false, false);
    }

    @Bean
    public FanoutExchange rabbitmqFanoutExchange() {
        return new FanoutExchange(RabbitMQConfig.RABBITMQ_FANOUT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindFanoutA() {
        return BindingBuilder
                .bind(rabbitmqFanoutQueueA())
                .to(rabbitmqFanoutExchange());
    }

    @Bean
    public Binding bindFanoutB() {
        return BindingBuilder
                .bind(rabbitmqFanoutQueueB())
                .to(rabbitmqFanoutExchange());
    }

    // Topic Exchange
    @Bean
    public Queue rabbitmqTopicQueueA() {
        return new Queue(RabbitMQConfig.RABBITMQ_TOPIC_QUEUE_A_NAME, true, false, false);
    }

    @Bean
    public Queue rabbitmqTopicQueueB() {
        return new Queue(RabbitMQConfig.RABBITMQ_TOPIC_QUEUE_B_NAME, true, false, false);
    }

    @Bean
    public Queue rabbitmqTopicQueueB2() {
        return new Queue(RabbitMQConfig.RABBITMQ_TOPIC_QUEUE_B2_NAME, true, false, false);
    }

    @Bean
    public TopicExchange rabbitmqTopicExchange() {
        return new TopicExchange(RabbitMQConfig.RABBITMQ_TOPIC_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindTopicA() {
        return BindingBuilder
                .bind(rabbitmqTopicQueueA())
                .to(rabbitmqTopicExchange())
                .with("a.#");
    }

    @Bean
    public Binding bindTopicB() {
        return BindingBuilder
                .bind(rabbitmqTopicQueueB())
                .to(rabbitmqTopicExchange())
                .with("b.#");
    }

    @Bean
    public Binding bindTopicB2() {
        return BindingBuilder
                .bind(rabbitmqTopicQueueB2())
                .to(rabbitmqTopicExchange())
                .with("b.*");
    }

    // Header Exchange
    @Bean
    public Queue rabbitmqHeaderQueueA() {
        return new Queue(RabbitMQConfig.RABBITMQ_HEADER_QUEUE_A_NAME, true, false, false);
    }

    @Bean
    public Queue rabbitmqHeaderQueueB() {
        return new Queue(RabbitMQConfig.RABBITMQ_HEADER_QUEUE_B_NAME, true, false, false);
    }

    @Bean
    public HeadersExchange rabbitmqHeaderExchange() {
        return new HeadersExchange(RabbitMQConfig.RABBITMQ_HEADER_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Binding bindHeaderA() {
        Map<String, Object> map = new HashMap<>();
        map.put("header_a", "headers-exchanger");
        map.put("header_b", "headers-queue-a");
        return BindingBuilder
                .bind(rabbitmqHeaderQueueA())
                .to(rabbitmqHeaderExchange())
                .whereAll(map).match();
    }

    @Bean
    public Binding bindHeaderB() {
        Map<String, Object> map = new HashMap<>();
        map.put("header_a", "headers-exchanger");
        map.put("header_b", "headers-queue-b");
        return BindingBuilder
                .bind(rabbitmqHeaderQueueB())
                .to(rabbitmqHeaderExchange())
                .whereAny(map).match();
    }

    // 只有当 rabbitAdmin.setAutoStartup() 设为 true Spring 才会加载 RabbitAdmin.
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        rabbitAdmin.declareExchange(rabbitmqDirectExchange());
        rabbitAdmin.declareExchange(rabbitmqFanoutExchange());
        rabbitAdmin.declareExchange(rabbitmqTopicExchange());
        rabbitAdmin.declareExchange(rabbitmqHeaderExchange());

        rabbitAdmin.declareQueue(rabbitmqDirectQueue());
        rabbitAdmin.declareQueue(rabbitmqFanoutQueueA());
        rabbitAdmin.declareQueue(rabbitmqFanoutQueueB());
        rabbitAdmin.declareQueue(rabbitmqTopicQueueA());
        rabbitAdmin.declareQueue(rabbitmqTopicQueueB());
        rabbitAdmin.declareQueue(rabbitmqTopicQueueB2());
        rabbitAdmin.declareQueue(rabbitmqHeaderQueueA());
        rabbitAdmin.declareQueue(rabbitmqHeaderQueueB());
        return null;
    }
}
