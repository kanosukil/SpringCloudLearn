package com.demo.springcloud.service.Impl;

import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.feignclient.ElasticSearchClient;
import com.demo.springcloud.service.ElasticSearchService;
import com.demo.springcloud.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);
    @Resource
    private ElasticSearchClient client;

    @Override
    public List<TorrentDTO> search(String search) {
        ResultVO res = client.search(search);
        if (res.getCode().equals(200)) {
            return res.getTorrentVOS();
        } else {
            return null;
        }
    }

    @Override
    public Boolean add(TorrentDTO torrent) {
        return client.add(torrent).getCode().equals(200);
    }


    @Override
    public Boolean adds(String json) {
        return client.adds(json).getCode().equals(200);
    }


}
