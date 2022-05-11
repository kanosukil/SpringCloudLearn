package org.example.service;

import org.springframework.stereotype.Service;

@Service
public interface DirectExchangeService {
    String sendMsg(String msg) throws Exception;
}
