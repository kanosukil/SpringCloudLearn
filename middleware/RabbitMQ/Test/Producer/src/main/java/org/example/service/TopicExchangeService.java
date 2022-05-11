package org.example.service;

import org.springframework.stereotype.Service;

@Service
public interface TopicExchangeService {
    String sendMsg(String msg, String routingKey) throws Exception;
}
