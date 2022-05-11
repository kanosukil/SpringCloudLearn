package com.demo.springcloud.controller;

import com.demo.springcloud.DTO.ListTorrentDTO;
import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.service.ElasticSearchService;
import com.demo.springcloud.service.SpiderService;
import com.demo.springcloud.vo.ResultVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interaction/search")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Resource
    private SpiderService spider;
    @Resource
    private ElasticSearchService es;

    private void setSuccessful(List<TorrentDTO> list, ResultVO res, boolean i) {
        res.setCode(200);
        res.setTorrentVOS(list);
        String msg;
        if (i) {
            msg = "Successful!";

        } else {
            msg = "Successful, but don`t have the result.";
        }
        res.setMsg(msg);
    }

    private void setException(Exception e, ResultVO res) {
        logger.error("Error: {}", e.toString());
        res.setCode(500);
        res.setTorrentVOS(null);
        res.setMsg("Error: " + e);
    }

    @GetMapping("/find")
    public ResultVO find(@RequestParam("search") String search) {
        ResultVO res = new ResultVO();
        try {
            List<TorrentDTO> torrents = es.search(search);
            if (torrents != null && !torrents.isEmpty()) {
                setSuccessful(torrents, res, true);
            } else if (torrents != null) {
                setSuccessful(torrents, res, false);
            } else {
                List<TorrentDTO> list = spider.get(search);
                if (list == null || list.isEmpty()) {
                    throw new Exception("Spider Error!");
                } else {
                    Thread.sleep(1000);
                    torrents = es.search(search);
                    if (torrents == null) {
                        throw new Exception("Search Error!");
                    } else setSuccessful(torrents, res, !torrents.isEmpty());
                }
            }
        } catch (Exception ex) {
            setException(ex, res);
        }
        return res;
    }

    @PostMapping("/add_from_spider")
    public ResultVO addSpider(@RequestBody Map<String, Object> search) {
        ResultVO res = new ResultVO();
        logger.info("Search: {}", search.get("search"));
        try {
            List<TorrentDTO> list = spider.get(search.get("search").toString());
            if (list != null && !list.isEmpty()) {
                setSuccessful(list, res, true);
            } else if (list != null) {
                setSuccessful(list, res, false);
            } else {
                throw new Exception("Spider Error!");
            }
        } catch (Exception e) {
            setException(e, res);
        }
        return res;
    }

    @PostMapping("/add")
    public ResultVO add(@RequestBody TorrentDTO torrent) {
        ResultVO res = new ResultVO();
        try {
            if (es.add(torrent)) {
                setSuccessful(null, res, true);
            } else {
                throw new Exception("ElasticSearch add Error!");
            }
        } catch (Exception e) {
            setException(e, res);
        }
        return res;
    }

    @PostMapping("/adds")
    @ResponseBody
    public ResultVO adds(@RequestBody ListTorrentDTO list) {
        ResultVO res = new ResultVO();
        try {
            if (es.adds(new ObjectMapper().writeValueAsString(list.getTorrents()))) {
                setSuccessful(null, res, true);
            } else {
                throw new Exception("ElasticSearch adds Error!");
            }
        } catch (Exception e) {
            setException(e, res);
        }
        return res;
    }
}
