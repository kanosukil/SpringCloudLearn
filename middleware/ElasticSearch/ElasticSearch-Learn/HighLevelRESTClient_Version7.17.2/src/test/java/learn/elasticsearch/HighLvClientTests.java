package learn.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.elasticsearch.entity.Person;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.GetSourceRequest;
import org.elasticsearch.client.core.GetSourceResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@SpringBootTest
public class HighLvClientTests {

    private static final Logger logger = LoggerFactory.getLogger(HighLvClientTests.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    @Resource
    private RestHighLevelClient client;

    /*
        索引
     */

    // 创建索引
    @Test
    void indexCreate() throws IOException {
        // 创建请求
        CreateIndexRequest request
                = new CreateIndexRequest("test2");
        // 客户端执行请求 获得响应
        CreateIndexResponse response
                = client.indices().create(request, RequestOptions.DEFAULT);
        logger.info("Response: {}", response.isAcknowledged());
    }

    // 获得索引 判断索引是否存在
    @Test
    void indexExists() throws IOException {
        GetIndexRequest request
                = new GetIndexRequest("test2");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (exists) {
            GetIndexResponse response
                    = client.indices().get(request, RequestOptions.DEFAULT);
            logger.info("Response Length: {}", response.getIndices().length);
            for (String res : response.getIndices()) {
                logger.info("Indices: {}", res);
            }
        } else {
            logger.info("The index {} does not exist.", request.indices()[0]);
        }
    }

    // 删除索引
    @Test
    void indexDelete() throws IOException {
        DeleteIndexRequest request
                = new DeleteIndexRequest("test2");
        AcknowledgedResponse response
                = client.indices().delete(request, RequestOptions.DEFAULT);
        logger.info("Response: {}", response.isAcknowledged());
    }

    /*
        文档
     */

    /*
        IndexRequest 可使用不同的数据类型操作 ElasticSearch
     */
    // 创建文档 也可更新文档(全更新, 覆盖旧的文档)
    @Test
    void docCreate_ByObject() throws IOException {
        // 创建对象
        Person person = new Person("巴姆彬鸥", 3, null);
        // 创建请求
        IndexRequest request = new IndexRequest("test2");
        boolean exists = client.indices().exists(new GetIndexRequest(request.index()), RequestOptions.DEFAULT);
        if (exists) {
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
    void docCreate_ByJsonString() throws IOException {
        IndexRequest request = new IndexRequest("test2");
        request.id("1031");
        String json =
                "{" +
                        "\"name\": \"刘乐\"," +
                        "\"age\": 42," +
                        "\"job\": \"Teacher\"" +
                        "}";
        request.source(json, XContentType.JSON);
        if (client.indices().exists(new GetIndexRequest(request.index()), RequestOptions.DEFAULT)) {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
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
        }
    }

    @Test
    void docCreate_ByMap() throws IOException {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "陈天乐");
        jsonMap.put("age", 23);
        jsonMap.put("job", "Cleaner");
        IndexRequest request = new IndexRequest("test2")
                .id("1032")
                .source(jsonMap);
        if (client.indices().exists(new GetIndexRequest(request.index()), RequestOptions.DEFAULT)) {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
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
        }
    }

    @Test
    void docCreate_ByXContentBuilder() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("name", "张乐轩");
            builder.field("age", 39);
            builder.field("job", "Teacher");
            builder.timeField("createDate", new Date());
        }
        builder.endObject();
        IndexRequest request = new IndexRequest("test2")
                .id("1033")
                .source(builder);
        if (client.indices().exists(new GetIndexRequest(request.index()), RequestOptions.DEFAULT)) {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
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
        }
    }

    @Test
    void docCreate_ByKeyPairs() throws IOException {
        IndexRequest request = new IndexRequest("test2")
                .id("1034")
                .source("name", "周张陈",
                        "age", 26,
                        "job", "Programmer");
        if (client.indices().exists(new GetIndexRequest(request.index()), RequestOptions.DEFAULT)) {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
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
        }
    }

    // 获取文档 只判断是否存在和获取基本信息 (捕获的数据)
    @Test
    void docExist() throws IOException {
        GetRequest request = new GetRequest("test2", "1030");
        // 执行请求 判断是否存在
        if (client.exists(request, RequestOptions.DEFAULT)) {
            // 不获取 _sources 上下文 即不获得文档数据
            request.fetchSourceContext(new FetchSourceContext(false));
            request.storedFields("_none_");
            // 执行请求
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            logger.info("Response: {}", response.toString());
            logger.info("Response is exists: {}", response.isExists());
            logger.info("Response index: {}", response.getIndex());
            logger.info("Response id: {}", response.getId());
            // 由于前面设置了不返回 _source 上下文, 因此此处显示为空
            logger.info("Response source is empty: {}", response.isSourceEmpty());
            logger.info("Response source: {}", response.getSource());
            logger.info("Response Seq No: {}", response.getSeqNo());
            logger.info("Response fields: {}", response.getFields().size());
            logger.info("Response type: {}", response.getType());
            logger.info("Response version: {}", response.getVersion());
            logger.info("Response primary term: {}", response.getPrimaryTerm());
        } else {
            logger.info("The index {} or the document id:{} is not exists.", request.index(), request.id());
        }
    }

    // 获取文档数据
    // 1 GetRequest & GetResponse
    @Test
    void docGet1() throws IOException {
        GetRequest request = new GetRequest("test2", "1030");
        // 执行请求 判断是否存在
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        if (response.isExists()) {
            // 执行请求
            logger.info("Response: {}", response);
            logger.info("Response index: {}", response.getIndex());
            logger.info("Response id: {}", response.getId());
            logger.info("Response source is empty: {}", response.isSourceEmpty());
            // logger.info("Response source: {}", response.getSource()); // 输出的 key value 都非字符串 mapper 无法解析
            logger.info("Response source: {}", mapper.readValue(response.getSourceAsString(), Person.class));
            logger.info("Response Seq No: {}", response.getSeqNo());
            logger.info("Response fields: {}", response.getFields().size());
            logger.info("Response type: {}", response.getType());
            logger.info("Response version: {}", response.getVersion());
            logger.info("Response primary term: {}", response.getPrimaryTerm());
        } else {
            logger.info("The index {} or the document id:{} is not exists.", request.index(), request.id());
        }
    }

    // 2 GetSourceRequest & GetSourceResponse
    @Test
    void docGet2() throws IOException {
        GetSourceRequest request = new GetSourceRequest("test2", "1030");
        GetSourceResponse response = client.getSource(request, RequestOptions.DEFAULT);
        logger.info("Response: {}", response.toString());
        logger.info("Response Source: {}", response.getSource());
    }

    // 更新文档
    @Test
    void docUpdate() throws IOException {
        UpdateRequest request = new UpdateRequest("test2", "1030");
        if (client.exists(new GetRequest(request.index(), request.id()), RequestOptions.DEFAULT)) {
            request.doc(mapper.writeValueAsString(new Person("小洋人", 55, "Yangtze")), XContentType.JSON);
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            logger.info("Res: {}", response);
            logger.info("Res status: {}", response.status());
            logger.info("Res is Fragment: {}", response.isFragment());
            logger.info("Res version: {}", response.getVersion());
            logger.info("Res Seq No: {}", response.getSeqNo());
            logger.info("Res Shard Info: {}", response.getShardInfo());
            logger.info("Res result: {}", response.getGetResult());
            logger.info("Res id: {}", response.getId());
            logger.info("Res index: {}", response.getIndex());
            logger.info("Res PrimaryTerm: {}", response.getPrimaryTerm());
            logger.info("Res has references: {}", response.hasReferences());
        } else {
            logger.info("The index {} or the document id:{} is not exists.", request.index(), request.id());
        }

    }

    // 删除文档 (响应的信息和 更新 一样)
    @Test
    void docDelete() throws IOException {
        DeleteRequest request = new DeleteRequest("test2", "1030");
        if (client.indices().exists(new GetIndexRequest(request.index()), RequestOptions.DEFAULT)) {
            request.timeout("5s");
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            logger.info("Response: {}", response.toString());
            logger.info("Response status: {}", response.status());
        } else {
            logger.info("Indices is not exists.");
        }

    }

    // 批量创建文档
    @Test
    void docBulkCreate() throws IOException {
        // 创建 bulk 批量请求对象
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        // 创建数据
        List<Person> personList = new ArrayList<>();
        personList.add(new Person("杨洋", 34, "Swimmer"));
        personList.add(new Person("周洋", 22, "Student"));
        personList.add(new Person("郝彬", 18, "Soldier"));
        personList.add(new Person("海彬", 21, "Student"));
        personList.add(new Person("刘洋彬", 49, "PM"));
        // 将数据加入请求
        int i = 0;
        // 批处理
        for (Person p : personList) {
            request.add(
                    new IndexRequest("test2")
                            .id(String.valueOf(1025 + i++))
                            .source(mapper.writeValueAsString(p), XContentType.JSON)
            );
            // request.add(new UpdateRequest())
            // request.add(new DeleteRequest())
        }
        // 执行请求 并输出结果
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        logger.info("Status: {}", response.status());
        logger.info("Has Failures: {}", response.hasFailures());
        logger.info("Failure Message: {}", response.buildFailureMessage());
        logger.info("Is Fragment: {}", response.isFragment());
        logger.info("Took: {}", response.getTook()); // 请求执行耗时
        logger.info("Ingest Took: {}", response.getIngestTook());
        logger.info("Ingest Took (ms): {}", response.getIngestTookInMillis());
        for (BulkItemResponse r : response.getItems()) {
            logger.info("-----------------------------------");
            logger.info("Bulk Item Response: {}", r.status());
            logger.info("Bulk Item Response id: {}", r.getId());
            logger.info("Bulk Item Response index: {}", r.getIndex());
            logger.info("Bulk Item Response item id: {}", r.getItemId());
            logger.info("Bulk Item Response is failed: {}", r.isFailed());
            logger.info("Bulk Item Response failure: {}", r.getFailure());
            logger.info("Bulk Item Response failure message: {}", r.getFailureMessage());
            logger.info("Bulk Item Response type: {}", r.getType());
            logger.info("Bulk Item Response Op type: {}", r.getOpType());
            logger.info("Bulk Item Response version: {}", r.getVersion());
        }
    }

    /*
        查询
     */
    @Test
    void search() throws IOException {
        SearchRequest request = new SearchRequest("test2"); // 可设置一个或多个索引 也可后面再配置索引
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 配置 SearchSourceBuilder
//        sourceBuilder.query(QueryBuilders.termQuery("name", "洋"))
        sourceBuilder.query(QueryBuilders.boolQuery())
                // QueryBuilders 工具类, 内置许多默认 QueryBuilder
                // 可以自己配置 QueryBuilder
                // sourceBuilder.query(QueryBuilders.matchAllQuery()) // 设置 SearchSourceBuilder 为 match_all 查询
                // sourceBuilder.query(QueryBuilders.wildcardQuery("name.keyword", "?洋?"))
                // sourceBuilder.query(QueryBuilders.matchQuery("key", "value"));
                .from(0) // 页
                .size(2) // 页显示的文档数
                .sort("name.keyword", SortOrder.ASC) // 排序 ASC: 小 -> 大 DESC: 大 -> 小
                .highlighter( // 'highlight': ...
                        new HighlightBuilder()
                                .preTags("<a id='highlight'>")
                                .postTags("</a>")
                                .field("name")
                        // 可使用 HighlightBuilder.Field 对象指定 并配置参数
                        // .field(new HighlightBuilder.Field("key")
                        //        .highlighterType("unified") // 有指定 String 参数
                        // )
                )
                .fetchSource( // '_source': ...
                        new FetchSourceContext(true, // false 则不返回 _source 数据
                                new String[]{"name"}, // include
                                new String[]{"age"})) // exclude
                        /*
                            可选参数列表
                                FetchSourceContext(Boolean fetchSource)
                                FetchSourceContext(Boolean fetchSource, String[] include, String[] exclude)
                                FetchSourceContext(String[] include, String[] exclude)
                         */
        // 配置聚合查询, 内部为 AggregationBuilder 的实现类
        // .aggregation(
        //        new AggregationBuilder() 实现类
        // )
        ;
        sourceBuilder.timeout(TimeValue.timeValueSeconds(3)); // 超时时间
        // 配置 SearchRequest
        request.source(sourceBuilder); // 将 SearchSourceBuilder 加入 SearchRequest
        // request.indices(String 索引, ...);

        // 执行请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        logger.info("Response: {}", response);
        logger.info("Response Clusters: {}", response.getClusters());
        logger.info("Response Took: {}", response.getTook());
        logger.info("Response Status: {}", response.status());
        logger.info("Response is Time Out: {}", response.isTimedOut());
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            logger.info("----------------------------------");
            logger.info("Hits: {}", hit.toString());
            logger.info("Hits id: {}", hit.getId());
            logger.info("Hits doc id: {}", hit.docId());
            logger.info("Hits index: {}", hit.getIndex());
            logger.info("Hits Seq No: {}", hit.getSeqNo());
            logger.info("Hits source: {}", hit.getSourceAsString());
            logger.info("Hits Cluster Alias: {}", hit.getClusterAlias());
            logger.info("Hits version: {}", hit.getVersion());
            logger.info("Hits Document Fields: {}", hit.getDocumentFields());
            logger.info("Hits Explanation: {}", hit.getExplanation());
            logger.info("Hits InnerHits: {}", hit.getInnerHits());
            logger.info("Hits Shard: {}", hit.getShard());
        }
    }
    
}
