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
        ??????
     */

    // ????????????
    @Test
    void indexCreate() throws IOException {
        // ????????????
        CreateIndexRequest request
                = new CreateIndexRequest("test2");
        // ????????????????????? ????????????
        CreateIndexResponse response
                = client.indices().create(request, RequestOptions.DEFAULT);
        logger.info("Response: {}", response.isAcknowledged());
    }

    // ???????????? ????????????????????????
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

    // ????????????
    @Test
    void indexDelete() throws IOException {
        DeleteIndexRequest request
                = new DeleteIndexRequest("test2");
        AcknowledgedResponse response
                = client.indices().delete(request, RequestOptions.DEFAULT);
        logger.info("Response: {}", response.isAcknowledged());
    }

    /*
        ??????
     */

    /*
        IndexRequest ???????????????????????????????????? ElasticSearch
     */
    // ???????????? ??????????????????(?????????, ??????????????????)
    @Test
    void docCreate_ByObject() throws IOException {
        // ????????????
        Person person = new Person("????????????", 3, null);
        // ????????????
        IndexRequest request = new IndexRequest("test2");
        boolean exists = client.indices().exists(new GetIndexRequest(request.index()), RequestOptions.DEFAULT);
        if (exists) {
            // ??????????????????
            request.id("1030");
            request.timeout("10s"); // = request.timeout(TimeValue.timeValueSeconds(10));
            // ???????????????????????? Json
            request.source(mapper.writeValueAsString(person), XContentType.JSON);
            // ????????????
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            // ????????????
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
                        "\"name\": \"??????\"," +
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
        jsonMap.put("name", "?????????");
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
            builder.field("name", "?????????");
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
                .source("name", "?????????",
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

    // ???????????? ?????????????????????????????????????????? (???????????????)
    @Test
    void docExist() throws IOException {
        GetRequest request = new GetRequest("test2", "1030");
        // ???????????? ??????????????????
        if (client.exists(request, RequestOptions.DEFAULT)) {
            // ????????? _sources ????????? ????????????????????????
            request.fetchSourceContext(new FetchSourceContext(false));
            request.storedFields("_none_");
            // ????????????
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            logger.info("Response: {}", response.toString());
            logger.info("Response is exists: {}", response.isExists());
            logger.info("Response index: {}", response.getIndex());
            logger.info("Response id: {}", response.getId());
            // ?????????????????????????????? _source ?????????, ????????????????????????
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

    // ??????????????????
    // 1 GetRequest & GetResponse
    @Test
    void docGet1() throws IOException {
        GetRequest request = new GetRequest("test2", "1030");
        // ???????????? ??????????????????
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        if (response.isExists()) {
            // ????????????
            logger.info("Response: {}", response);
            logger.info("Response index: {}", response.getIndex());
            logger.info("Response id: {}", response.getId());
            logger.info("Response source is empty: {}", response.isSourceEmpty());
            // logger.info("Response source: {}", response.getSource()); // ????????? key value ??????????????? mapper ????????????
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

    // ????????????
    @Test
    void docUpdate() throws IOException {
        UpdateRequest request = new UpdateRequest("test2", "1030");
        if (client.exists(new GetRequest(request.index(), request.id()), RequestOptions.DEFAULT)) {
            request.doc(mapper.writeValueAsString(new Person("?????????", 55, "Yangtze")), XContentType.JSON);
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

    // ???????????? (?????????????????? ?????? ??????)
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

    // ??????????????????
    @Test
    void docBulkCreate() throws IOException {
        // ?????? bulk ??????????????????
        BulkRequest request = new BulkRequest();
        request.timeout("10s");
        // ????????????
        List<Person> personList = new ArrayList<>();
        personList.add(new Person("??????", 34, "Swimmer"));
        personList.add(new Person("??????", 22, "Student"));
        personList.add(new Person("??????", 18, "Soldier"));
        personList.add(new Person("??????", 21, "Student"));
        personList.add(new Person("?????????", 49, "PM"));
        // ?????????????????????
        int i = 0;
        // ?????????
        for (Person p : personList) {
            request.add(
                    new IndexRequest("test2")
                            .id(String.valueOf(1025 + i++))
                            .source(mapper.writeValueAsString(p), XContentType.JSON)
            );
            // request.add(new UpdateRequest())
            // request.add(new DeleteRequest())
        }
        // ???????????? ???????????????
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        logger.info("Status: {}", response.status());
        logger.info("Has Failures: {}", response.hasFailures());
        logger.info("Failure Message: {}", response.buildFailureMessage());
        logger.info("Is Fragment: {}", response.isFragment());
        logger.info("Took: {}", response.getTook()); // ??????????????????
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
        ??????
     */
    @Test
    void search() throws IOException {
        SearchRequest request = new SearchRequest("test2"); // ?????????????????????????????? ???????????????????????????
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // ?????? SearchSourceBuilder
//        sourceBuilder.query(QueryBuilders.termQuery("name", "???"))
        sourceBuilder.query(QueryBuilders.boolQuery())
                // QueryBuilders ?????????, ?????????????????? QueryBuilder
                // ?????????????????? QueryBuilder
                // sourceBuilder.query(QueryBuilders.matchAllQuery()) // ?????? SearchSourceBuilder ??? match_all ??????
                // sourceBuilder.query(QueryBuilders.wildcardQuery("name.keyword", "?????"))
                // sourceBuilder.query(QueryBuilders.matchQuery("key", "value"));
                .from(0) // ???
                .size(2) // ?????????????????????
                .sort("name.keyword", SortOrder.ASC) // ?????? ASC: ??? -> ??? DESC: ??? -> ???
                .highlighter( // 'highlight': ...
                        new HighlightBuilder()
                                .preTags("<a id='highlight'>")
                                .postTags("</a>")
                                .field("name")
                        // ????????? HighlightBuilder.Field ???????????? ???????????????
                        // .field(new HighlightBuilder.Field("key")
                        //        .highlighterType("unified") // ????????? String ??????
                        // )
                )
                .fetchSource( // '_source': ...
                        new FetchSourceContext(true, // false ???????????? _source ??????
                                new String[]{"name"}, // include
                                new String[]{"age"})) // exclude
                        /*
                            ??????????????????
                                FetchSourceContext(Boolean fetchSource)
                                FetchSourceContext(Boolean fetchSource, String[] include, String[] exclude)
                                FetchSourceContext(String[] include, String[] exclude)
                         */
        // ??????????????????, ????????? AggregationBuilder ????????????
        // .aggregation(
        //        new AggregationBuilder() ?????????
        // )
        ;
        sourceBuilder.timeout(TimeValue.timeValueSeconds(3)); // ????????????
        // ?????? SearchRequest
        request.source(sourceBuilder); // ??? SearchSourceBuilder ?????? SearchRequest
        // request.indices(String ??????, ...);

        // ????????????
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
