package com.demo.springcloud.controller;

import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.config.RabbitMQTotalConfig;
import com.demo.springcloud.service.SpiderService;
import com.demo.springcloud.vo.ResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/spider")
public class SpiderController {
    private static final Logger logger = LoggerFactory.getLogger(SpiderController.class);
    @Resource
    private SpiderService service;
    @Resource
    private RabbitTemplate template;

    // /get?search=xxx
    @GetMapping("/get")
    public ResultVO search(@RequestParam("search") String search) {
        // 获取数据
        ResultVO res = new ResultVO();
        try {
            logger.info("Search: {}", search);
            List<TorrentDTO> data = service.getData(search);
            // 将数据传入 ElasticSearch
            logger.info("Data length: {}", data.size());
            template.convertAndSend(
                    RabbitMQTotalConfig.RabbitMQ_Data_DirectExchange,
                    RabbitMQTotalConfig.RabbitMQ_Data_Routing,
                    new ObjectMapper().writeValueAsString(data));
            res.setCode(200);
            res.setTorrentVOS(data);
            res.setMsg("Get!");
        } catch (Exception e) {
            logger.error("Error: {}", e.toString());
            res.setCode(500);
            res.setTorrentVOS(null);
            res.setMsg("Error: " + e);
        }
        return res;
    }
}
