package com.demo.springcloud.service.Impl;

import com.demo.springcloud.DTO.TorrentDTO;
import com.demo.springcloud.service.SpiderService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpiderServiceImpl implements SpiderService {
    private static final Logger logger = LoggerFactory.getLogger(SpiderServiceImpl.class);

    @Override
    public List<TorrentDTO> getData(String search) throws IOException {
        String url_root = "https://acg.rip";
        String url_search = url_root + "/?term=" + search;
        // 返回的即为 JS 的 document 对象
        Document doc = Jsoup.parse(new URL(url_search), 30000);
        Elements trs = doc.getElementsByTag("tr");

        List<TorrentDTO> objectList = new ArrayList<>();
        for (Element tr : trs) {
            String title;
            String size;
            String torrent = url_root;
            try {
                title = tr.getElementsByClass("title").get(0)
                        .getElementsByClass("title").get(0)
                        .getElementsByTag("a")
                        .text();
                size = tr.getElementsByClass("size").eq(0)
                        .text();
                torrent += tr.getElementsByClass("action").get(0)
                        .getElementsByTag("a").eq(0)
                        .attr("href");
            } catch (IndexOutOfBoundsException ex) {
                logger.error("Error: {}", ex.toString());
                continue;
            }
            TorrentDTO object = new TorrentDTO(title, size, torrent);
            logger.info("ACG.RIP: {}", object);
            objectList.add(object);
        }
        return objectList;
    }
}
