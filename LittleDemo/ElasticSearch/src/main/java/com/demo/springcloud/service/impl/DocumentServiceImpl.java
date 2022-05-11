package com.demo.springcloud.service.impl;

import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Override
    public Boolean createDocument(String index, Object o) throws IOException {
        return createDocument(index, o, null);
    }

    @Override
    public Boolean createDocument(String index, Object o, Integer id) throws IOException {
        String result = client.index(
                        new IndexRequest(index)
                                .source(mapper.writeValueAsString(o), XContentType.JSON)
                                .id(String.valueOf(id))
                                .timeout(TimeValue.timeValueSeconds(5)),
                        RequestOptions.DEFAULT)
                .getResult()
                .toString();
        return result.equalsIgnoreCase("CREATED")
                || result.equalsIgnoreCase("UPDATED");
    }

    @Override
    public Boolean updateDocument(String index, Object o, Integer id) throws IOException {
        String result;
        try {
            result = client.update(
                            new UpdateRequest(index, String.valueOf(id))
                                    .doc(mapper.writeValueAsString(o), XContentType.JSON)
                                    .timeout(TimeValue.timeValueSeconds(5)),
                            RequestOptions.DEFAULT
                    )
                    .getResult()
                    .toString();
        } catch (IOException ex) {
            result = "Not Found";
        }
        return result.equalsIgnoreCase("UPDATED")
                || result.equalsIgnoreCase("NOOP");
    }

    @Override
    public Boolean deleteDocument(String index, Integer id) throws IOException {
        String result = client.delete(
                        new DeleteRequest(index, String.valueOf(id))
                                .timeout(TimeValue.timeValueSeconds(5)),
                        RequestOptions.DEFAULT)
                .getResult()
                .toString();
        return result.equalsIgnoreCase("DELETED");
    }

    @Override
    public GetResponse getDocument(String index, Integer id) throws IOException {
        return client.get(
                new GetRequest(index, String.valueOf(id)),
                RequestOptions.DEFAULT);
    }

    @Override
    public Boolean bulkDocument(String index, List<TorrentDTO> os) throws IOException {
        BulkRequest request = new BulkRequest(index);
        for (Object o : os) {
            request.add(
                    new IndexRequest()
                            .source(mapper.writeValueAsString(o), XContentType.JSON)
                            .timeout(TimeValue.timeValueSeconds(10))
            );
        }
        try {
            client.bulkAsync(request, RequestOptions.DEFAULT, new ActionListener<>() {
                @Override
                public void onResponse(BulkResponse response) {
                    if (response.hasFailures()) {
                        for (BulkItemResponse item : response.getItems()) {
                            if (item.isFailed()) {
                                logger.error("ID: {} failed created.", item.getItemId());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    logger.error("Error: {}", e.toString());
                }
            });
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
