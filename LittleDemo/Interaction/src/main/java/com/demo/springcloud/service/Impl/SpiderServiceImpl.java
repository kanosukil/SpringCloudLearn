package com.demo.springcloud.service.Impl;

import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.feignclient.SpiderClient;
import com.demo.springcloud.service.SpiderService;
import com.demo.springcloud.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpiderServiceImpl implements SpiderService {
    private static final Logger logger = LoggerFactory.getLogger(SpiderServiceImpl.class);
    @Resource
    private SpiderClient client;

    @Override
    public List<TorrentDTO> get(String search) {
        ResultVO res = client.search(search);
        if (res.getCode().equals(200)) {
            return res.getTorrentVOS();
        } else {
            return null;
        }
    }
}
