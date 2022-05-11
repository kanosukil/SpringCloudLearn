package org.example.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface HeaderExchangeService {
    String sendMsg(String msg, Map<String, Object> map) throws Exception;
}
