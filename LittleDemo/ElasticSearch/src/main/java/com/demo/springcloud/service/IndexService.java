package com.demo.springcloud.service;

import org.elasticsearch.client.indices.GetIndexResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface IndexService {
    Boolean isExists(String index) throws IOException;

    Boolean createIndex(String index) throws IOException;

    GetIndexResponse getIndex(String index) throws IOException;

    Boolean deleteIndex(String index) throws IOException;
}
