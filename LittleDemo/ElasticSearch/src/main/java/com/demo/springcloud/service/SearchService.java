package com.demo.springcloud.service;

import com.demo.springcloud.DTO.TorrentDTO;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface SearchService {
    SearchResponse getResponse(String index, String search) throws IOException;

    SearchHit[] getHits(String index, String search) throws IOException;

    List<TorrentDTO> getResult(String index, String search) throws IOException;

    String getResultAsString(String index, String search) throws IOException;

    Map<String, Object> getResultAsMap(String index, String search) throws IOException;

    List<TorrentDTO> getResultWithHighLight(String index, String search) throws IOException;
}
