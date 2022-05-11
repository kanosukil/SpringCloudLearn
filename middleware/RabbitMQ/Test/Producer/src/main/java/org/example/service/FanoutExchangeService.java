package org.example.service;

import org.springframework.stereotype.Service;

@Service
public interface FanoutExchangeService {
    String sendMsg(String msg) throws Exception;
}
