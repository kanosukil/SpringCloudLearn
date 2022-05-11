package com.demo.springcloud.controller;

import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.config.RabbitMQTotalConfig;
import com.demo.springcloud.service.DocumentService;
import com.demo.springcloud.service.IndexService;
import com.demo.springcloud.service.SearchService;
import com.demo.springcloud.vo.ResultVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/elasticsearch")
public class ElasticSearchController {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);
    private static final String INDEX = "Torrent".toLowerCase();
    @Resource
    private IndexService index;
    @Resource
    private DocumentService doc;
    @Resource
    private SearchService searchService;

    private void errorResult(Exception e, ResultVO res) {
        logger.error("Error: {}", e.toString());
        res.setCode(500);
        res.setTorrentVOS(null);
        res.setMsg("Error: " + e);
    }

    @GetMapping("/search")
    public ResultVO search(@RequestParam("keyword") String search) {
        ResultVO res = new ResultVO();
        try {
            if (index.isExists(INDEX)) {
                List<TorrentDTO> resList
                        = searchService
                        .getResultWithHighLight(INDEX, search);
                res.setCode(200);
                res.setTorrentVOS(resList);
                res.setMsg("Successful!");
            } else {
                if (index.createIndex(INDEX)) {
                    res.setCode(404);
                    res.setTorrentVOS(null);
                    res.setMsg("Index has just created, please create torrent data.");
                } else {
                    res.setCode(500);
                    res.setTorrentVOS(null);
                    res.setMsg("Create Index Error.");
                }
            }
        } catch (Exception e) {
            errorResult(e, res);
        }
        return res;
    }

    private void addDocument(TorrentDTO torrent, ResultVO res) throws IOException {
        if (doc.createDocument(INDEX, torrent)) {
            res.setCode(200);
            res.setTorrentVOS(null);
            res.setMsg("Successful!");
        } else {
            res.setCode(500);
            res.setTorrentVOS(null);
            res.setMsg("Add Document Error.");
        }
    }


    @PostMapping("/add")
    public ResultVO add(@RequestBody TorrentDTO torrent) {
        ResultVO res = new ResultVO();
        try {
            if (index.isExists(INDEX)) {
                addDocument(torrent, res);
            } else {
                if (index.createIndex(INDEX)) {
                    addDocument(torrent, res);
                } else {
                    res.setCode(500);
                    res.setTorrentVOS(null);
                    res.setMsg("Create Index Error.");
                }
            }
        } catch (Exception e) {
            errorResult(e, res);
        }
        return res;
    }

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQTotalConfig.RabbitMQ_Data_Queue))
    @RabbitHandler
    public void insertData(String json) throws IOException {
        List<TorrentDTO> list = new ObjectMapper().readValue(json, new TypeReference<>() {
        });
        if (doc.bulkDocument(INDEX, list)) {
            logger.info("Successful!");
        } else {
            logger.error("Error!");
        }
    }

    @PostMapping("/adds")
    public ResultVO adds(@RequestBody String json) {
        ResultVO res = new ResultVO();
        try {
            if (index.isExists(INDEX)) {
                insertData(json);
                res.setCode(200);
                res.setTorrentVOS(null);
                res.setMsg("Successful!");
            } else {
                if (index.createIndex(INDEX)) {
                    insertData(json);
                    res.setCode(200);
                    res.setTorrentVOS(null);
                    res.setMsg("Successful!");
                } else {
                    res.setCode(500);
                    res.setTorrentVOS(null);
                    res.setMsg("Create Index Error.");
                }
            }
        } catch (Exception e) {
            errorResult(e, res);
        }
        return res;
    }
}
