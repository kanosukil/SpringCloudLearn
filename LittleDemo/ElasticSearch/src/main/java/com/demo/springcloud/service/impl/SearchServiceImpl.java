package com.demo.springcloud.service.impl;

import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Override
    public SearchResponse getResponse(String index, String search) throws IOException {
        return client.search(
                new SearchRequest()
                        .source(new SearchSourceBuilder()
                                .query(QueryBuilders.matchQuery("title", search))
                                .timeout(TimeValue.timeValueSeconds(5))
                                .sort("title.keyword", SortOrder.ASC)
                                .highlighter(
                                        new HighlightBuilder()
                                                .field(new HighlightBuilder.Field("title")
                                                        .preTags("<span style='color: red'>")
                                                        .postTags("</span>")
                                                        .fragmentSize(150)))),
                RequestOptions.DEFAULT);
    }

    @Override
    public SearchHit[] getHits(String index, String search) throws IOException {
        SearchResponse response = getResponse(index, search);
        return response.getHits().getHits();
    }

    @Override
    public List<TorrentDTO> getResult(String index, String search) throws IOException {
        List<TorrentDTO> torrents = new ArrayList<>();
        for (SearchHit hit : getHits(index, search)) {
            TorrentDTO t = mapper.readValue(hit.getSourceAsString(), TorrentDTO.class);
            torrents.add(t);
        }
        return torrents;
    }

    @Override
    public String getResultAsString(String index, String search) throws IOException {
        StringBuilder str = new StringBuilder();
        for (SearchHit hit : getHits(index, search)) {
            str.append(hit.getSourceAsString()).append('\n');
        }
        return str.toString();
    }

    @Override
    public Map<String, Object> getResultAsMap(String index, String search) throws IOException {
        Map<String, Object> map = new HashMap<>();
        for (SearchHit hit : getHits(index, search)) {
            map.put(hit.getId(), hit.getSourceAsMap());
        }
        return map;
    }

    @Override
    public List<TorrentDTO> getResultWithHighLight(String index, String search) throws IOException {
        List<TorrentDTO> torrents = new ArrayList<>();
        for (SearchHit hit : getHits(index, search)) {
            TorrentDTO t = mapper.readValue(hit.getSourceAsString(),
                    TorrentDTO.class);
            HighlightField title = hit.getHighlightFields().get("title");
            StringBuilder torrent_title = new StringBuilder();
            for (Text text : title.getFragments()) {
                torrent_title.append(text);
            }
            t.setTitle(torrent_title.toString());
            torrents.add(t);
        }
        return torrents;
    }
}
