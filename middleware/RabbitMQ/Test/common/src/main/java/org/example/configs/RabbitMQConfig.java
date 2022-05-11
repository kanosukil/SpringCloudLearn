package org.example.configs;


public class RabbitMQConfig {
    // Direct Exchange
    public static final String RABBITMQ_DIRECT_QUEUE_NAME = "rabbitmqDirectQueue";
    public static final String RABBITMQ_DIRECT_EXCHANGE_NAME = "rabbitmqDirectExchange";
    public static final String RABBITMQ_DIRECT_EXCHANGE_ROUTING = "rabbitmqDirectRouting";

    // Fanout Exchange
    public static final String RABBITMQ_FANOUT_QUEUE_A_NAME = "rabbitmqFanoutQueue.A";
    public static final String RABBITMQ_FANOUT_QUEUE_B_NAME = "rabbitmqFanoutQueue.B";
    public static final String RABBITMQ_FANOUT_EXCHANGE_NAME = "rabbitmqFanoutExchange";

    // Topic Exchange
    public static final String RABBITMQ_TOPIC_QUEUE_A_NAME = "A.rabbitmq.topic.queue";
    public static final String RABBITMQ_TOPIC_QUEUE_B_NAME = "B.rabbitmq.topic.queue";
    public static final String RABBITMQ_TOPIC_QUEUE_B2_NAME = "B.rabbitmq.topic.queue-2";
    public static final String RABBITMQ_TOPIC_EXCHANGE_NAME = "rabbitmqTopicExchange";

    // Header Exchange
    public static final String RABBITMQ_HEADER_QUEUE_A_NAME = "rabbitmqHeaderQueue.A";
    public static final String RABBITMQ_HEADER_QUEUE_B_NAME = "rabbitmqHeaderQueue.B";
    public static final String RABBITMQ_HEADER_EXCHANGE_NAME = "rabbitmqHeaderExchange";
}
