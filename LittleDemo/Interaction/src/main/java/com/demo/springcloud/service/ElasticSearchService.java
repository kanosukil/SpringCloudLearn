package com.demo.springcloud.service;

import com.demo.springcloud.DTO.TorrentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ElasticSearchService {
    List<TorrentDTO> search(String search);

    Boolean add(TorrentDTO torrent);

    Boolean adds(String json);
}
