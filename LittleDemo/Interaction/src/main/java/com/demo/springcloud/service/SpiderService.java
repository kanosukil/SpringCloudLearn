package com.demo.springcloud.service;

import com.demo.springcloud.DTO.TorrentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SpiderService {
    List<TorrentDTO> get(String search);
}
