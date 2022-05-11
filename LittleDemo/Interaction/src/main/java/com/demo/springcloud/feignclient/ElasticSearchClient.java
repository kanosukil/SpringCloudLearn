package com.demo.springcloud.feignclient;

import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.vo.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("ElasticSearch")
public interface ElasticSearchClient {
    @GetMapping("/elasticsearch/search")
    ResultVO search(@RequestParam("keyword") String search);

    @PostMapping("/elasticsearch/add")
    ResultVO add(@RequestBody TorrentDTO torrent);

    @PostMapping("/elasticsearch/adds")
    ResultVO adds(@RequestBody String json);
}
