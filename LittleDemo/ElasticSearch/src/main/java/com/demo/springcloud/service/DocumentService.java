package com.demo.springcloud.service;

import com.demo.springcloud.DTO.TorrentDTO;
import org.elasticsearch.action.get.GetResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface DocumentService {
    Boolean createDocument(String index, Object o) throws IOException;

    Boolean createDocument(String index, Object o, Integer id) throws IOException;

    Boolean updateDocument(String index, Object o, Integer id) throws IOException;

    Boolean deleteDocument(String index, Integer id) throws IOException;

    GetResponse getDocument(String index, Integer id) throws IOException;

    Boolean bulkDocument(String index, List<TorrentDTO> os) throws IOException;
}
