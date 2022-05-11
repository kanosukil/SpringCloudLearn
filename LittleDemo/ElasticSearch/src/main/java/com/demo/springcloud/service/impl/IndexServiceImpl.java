package com.demo.springcloud.service.impl;

import com.demo.springcloud.service.IndexService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Override
    public Boolean isExists(String index) throws IOException {
        return client.indices()
                .exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
    }

    @Override
    public Boolean createIndex(String index) throws IOException {
        return client.indices()
                .create(new CreateIndexRequest(index), RequestOptions.DEFAULT)
                .isAcknowledged();
    }

    @Override
    public GetIndexResponse getIndex(String index) throws IOException {
        return client.indices()
                .get(new GetIndexRequest(index), RequestOptions.DEFAULT);
    }

    @Override
    public Boolean deleteIndex(String index) throws IOException {
        return client.indices()
                .delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT)
                .isAcknowledged();
    }
}
