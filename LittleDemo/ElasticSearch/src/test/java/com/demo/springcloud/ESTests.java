package com.demo.springcloud;

import com.demo.springcloud.DTO.TorrentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class ESTests {
    private static final Logger logger = LoggerFactory.getLogger(ESTests.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Test
    void search() throws IOException {
        SearchRequest request = new SearchRequest("torrent");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 配置 SearchSourceBuilder
        // 搜索
        builder.query(QueryBuilders.matchQuery("title", "巨人"))
                // 排序
                .sort("title.keyword", SortOrder.ASC)
                .highlighter(
                        new HighlightBuilder()
                                .field(new HighlightBuilder.Field("title")
                                        .preTags("<span style='color: red'>")
                                        .postTags("</span>"))
                                .requireFieldMatch(false) // 多个高亮显示
                                .fragmentSize(150)
                )
                .timeout(TimeValue.timeValueSeconds(5));
        // 配置 SearchRequest
        request.source(builder);
        // 执行 Request
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            TorrentDTO t = mapper.readValue(hit.getSourceAsString(), TorrentDTO.class);

            HighlightField title = hit.getHighlightFields().get("title");
            System.out.println(t.getTitle());
            System.out.println(title);
            StringBuilder torrent_title = new StringBuilder();
            for (Text text : title.getFragments()) {
                torrent_title.append(text);
            }
            t.setTitle(torrent_title.toString());
            logger.info("Title: {}", t.getTitle());
        }
    }

    @Test
    void ESTest() throws IOException {
        // 创建对象
        Person person = new Person("露娜", 13, null);
        // 创建请求
        IndexRequest request = new IndexRequest("test2");
        if (client.indices().exists(new GetIndexRequest(request.index()), RequestOptions.DEFAULT)) {
            // 设置请求选项
            request.id("1030");
            request.timeout("10s"); // = request.timeout(TimeValue.timeValueSeconds(10));
            // 必须将对象转换成 Json
            request.source(mapper.writeValueAsString(person), XContentType.JSON);
            // 发送请求
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            // 输出结果
            logger.info("Response: {}", response.toString());
            logger.info("Response Status: {}", response.status());
            logger.info("Response Shard Info: {}", response.getShardInfo());
            logger.info("Response Seq No: {}", response.getSeqNo());
            logger.info("Response id: {}", response.getId());
            logger.info("Response index: {}", response.getIndex());
            logger.info("Response Result: {}", response.getResult().toString());
            logger.info("Response Version: {}", response.getVersion());
            logger.info("Response is Fragment: {}", response.isFragment());
            logger.info("Response Primary Term: {}", response.getPrimaryTerm());
        } else {
            logger.info("The Indices {} dose not exists", request.index());
        }
    }

    @Test
    void Search() throws IOException {
        SearchRequest request = new SearchRequest("test2");
        request.source(new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .timeout(TimeValue.timeValueSeconds(5)));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits().getHits()) {
            logger.info("------------------------------------------------");
            try {
                logger.info("Hit:\n {}", mapper.readValue(hit.getSourceAsString(), Person.class));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @Test
    void delete() throws IOException {
        DeleteRequest request = new DeleteRequest("test2");
        request.id("1030");
        logger.info("Result: {}", client.delete(request, RequestOptions.DEFAULT).getResult());

    }

    @Test
    void updateDoc() throws IOException {
        String index = "test2";
        Integer id = 1040;
        Person o = new Person("测试", 200, "PersonOne");
        String result = client.delete(
                        new DeleteRequest(index, String.valueOf(id))
                                .timeout(TimeValue.timeValueSeconds(5)),
                        RequestOptions.DEFAULT)
                .getResult()
                .toString();
        logger.info("Res: {}", result);
    }

    private static class Person {
        private String name;
        private Integer age;
        private String job;

        public Person(String name, Integer age, String job) {
            this.name = name;
            this.age = age;
            this.job = job;
        }

        public Person() {
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", job='" + job + '\'' +
                    '}';
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }
    }
}
