package com.demo.springcloud.config;

public class RabbitMQTotalConfig {
    public static final String RabbitMQ_GetSearch_Queue = "Word_To_Data_Queue";
    public static final String RabbitMQ_GetSearch_DirectExchange = "Word_To_Data_Exchange";
    public static final String RabbitMQ_GetSearch_Routing = "Word_To_Data_Routing";

    public static final String RabbitMQ_Data_Queue = "Data_TO_ElasticSearch_Queue";
    public static final String RabbitMQ_Data_DirectExchange = "Data_TO_ElasticSearch_Exchange";
    public static final String RabbitMQ_Data_Routing = "Data_TO_ElasticSearch_Routing";

    public static final String RabbitMQ_Search_Queue = "Search_Queue";
    public static final String RabbitMQ_Search_Exchange = "Search_Exchange";
    public static final String RabbitMQ_Search_Routing = "Search_Routing";
}
