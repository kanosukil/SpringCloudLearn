package com.demo.springcloud.service;

import com.demo.springcloud.DTO.TorrentDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface SpiderService {
    List<TorrentDTO> getData(String search) throws IOException;
}
