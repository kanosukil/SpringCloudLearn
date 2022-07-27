package learn.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.NodeStatistics;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.HighlighterEncoder;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.util.ApiTypeHelper;
import learn.elasticsearch.entity.Person;
import learn.elasticsearch.entity.PersonDTO;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JavaAPITests {
    private static final Logger logger = LoggerFactory.getLogger(JavaAPITests.class);
    @Resource
    private ElasticsearchClient e_client;
    @Resource
    private ElasticsearchAsyncClient ea_client;

    // 查指定文档( term 精准指定)
    //
    @Test
    void searchTest() throws IOException {
        logger.info("Search Method: Term");
        // 创建 search 对象 (实体必须对应)
        SearchResponse<Person> term = e_client.search(s ->
                        s.index("test1") // 索引
                                .query(q -> // 搜索查询
                                        q.term(t -> // term 精确查询
                                                t.field("name.keyword") // .field("name.keyword") 可用
                                                        .value(v -> v.stringValue("周洋")) // 指定值
                                        )),
                Person.class);
        // 输出指定内容
        logTheRes(term);

        logger.info("Search Method: WildCard");
        // 创建 search 对象 (实体必须对应)
        SearchResponse<Person> wildcard = e_client.search(s ->
                        s.index("test1").query(q ->
                                q.wildcard(wc ->
                                        wc.field("name.keyword").value("?" + "洋"))), // 需要 keyword 类型的字符串才好看出效果
                Person.class);
        // 输出指定内容
        logTheRes(wildcard);

        logger.info("Search Method: match");
        // 创建 search 对象 (实体必须对应)
        SearchResponse<Person> match = e_client.search(s ->
                        s.index("test1").query(q ->
                                q.match(m ->
                                        m.field("name").query("洋"))),
                Person.class);
        // 输出指定内容
        logTheRes(match);

        SearchResponse<Person> complex = e_client.search(s ->
                        s.index("test1").query(q -> q
                                        .bool(b -> b
                                                .filter(f -> f
                                                        .range(r -> r
                                                                .field("age")
                                                                .gt(JsonData.of(18))
                                                                .lt(JsonData.of(30))))
                                                .must(m -> m
                                                        .term(t -> t
                                                                .field("name")
                                                                .value("洋")))
                                                .must(m -> m
                                                        .term(t -> t
                                                                .field("job")
                                                                .value("novelist")))))
                                .from(0)
                                .size(1)
                                .highlight(h -> h
                                        .fields("name", f -> f
                                                .preTags("<a id=\"highlight\">")
                                                .postTags("</a>"))
                                        .encoder(HighlighterEncoder.Html))
                , Person.class);
        logTheRes(complex);
    }

    private void logTheRes(SearchResponse<Person> searchResponse) {
        assert searchResponse.hits().total() != null;  // 搜索到的结果数
        logger.info("Total: {}", searchResponse.hits().total().value());
        for (Hit<Person> hit : searchResponse.hits().hits()) { // 遍历每一个匹配文档
            assert hit.source() != null;
            logger.info(hit.source().toString());
        }
    }

    // 创建索引
    @Test
    void indexCreateTest() throws IOException {
        // 创建索引 lambda 表达式
        // lambda 表达式的 参数使用 _N 表示 (深度级别)
        // 每一个 build() 都可使用 lambda 替换
        // 因此需要一眼即可识别的参数标识
        CreateIndexResponse res = e_client.indices().create(_1 ->
                _1.index("test2") // 索引名
                        .aliases("foo", // 别名
                                _2 -> _2.isWriteIndex(true)));
        /*
            上句 相当于
            CreateIndexResponse createResponse = client.indices().create(
                new CreateIndexRequest.Builder()
                    .index("test2")
                    .aliases("foo",
                        new Alias.Builder().isWriteIndex(true).build()
                    )
                    .build()
            );
         */
        // 将指定响应结果输出
        logger.info("Acknowledged: {}", res.acknowledged());
        // 重复创建将报错: ElasticsearchException: [es/indices.create] failed: [resource_already_exists_exception] index [...] already exists
    }

    // 删除索引
    @Test
    void indexDeleteTest() throws IOException {
        DeleteIndexResponse res = e_client.indices().delete(d -> d.index("test4"));
        logger.info("Acknowledged: {}", res.acknowledged());
        // 没找到将报错: ElasticsearchException: [es/indices.delete] failed: [index_not_found_exception] no such index [...]
    }

    // 查询索引
    @Test
    void indexGetTest() throws IOException {
        GetIndexResponse res = e_client.indices().get(e -> e.index("test1"));
        logger.info("Res: {}", res.result()); // 内存地址
    }

    // 判断索引是否存在
    @Test
    void indexExistsTest() throws IOException {
        BooleanResponse res = e_client.indices().exists(e -> e.index("test2"));
        logger.info("Res: {}", res.value());

    }

    // 创建文档
    // POST /索引/_doc/{id}
    @Test
    void documentCreateTest() throws IOException {
        IndexResponse res = e_client.index(e ->
//                e.index("test2").id("1020").document(new Person("洋洋", 22, "student")));
                e.index("test2")
                        .id("123")
                        .document(new PersonDTO(new Person("洋洋", 22, "student").toString())));
        logger.info("Res: {}", res.result().jsonValue());
        logger.info("{}", res.result().toString());
        // 批量插入: 利用 bulk
        /*
        List<FileSearch> fileSearchList = new ArrayList<>();
        List<BulkOperation> bulkOperationArrayList = new ArrayList<>();
        //遍历添加到bulk中
        for(FileSearch fileSearch : fileSearchList){
            bulkOperationArrayList.add(BulkOperation.of(o->o.index(i->i.document(fileSearch))));
        }

        BulkResponse bulkResponse = client.bulk(b -> b.index("filesearch")
                .operations(bulkOperationArrayList));
         */

//        List<BulkOperation> bulkOperationArrayList = new ArrayList<>();
//        bulkOperationArrayList.add(
//                BulkOperation.of(
//                        _1 -> _1.index(
//                                _2 -> _2.id("13")
//                                        .document(new Person("13", 13, "13")))));
//        bulkOperationArrayList.add(
//                BulkOperation.of(
//                        _1 -> _1.index(
//                                _2 -> _2.id("12")
//                                        .document(new Person("12", 12, "12")))));
//        bulkOperationArrayList.add
//                (BulkOperation.of(
//                        _1 -> _1.index(
//                                _2 -> _2.id("11")
//                                        .document(new Person("11", 11, "11")))));
//        bulkOperationArrayList.add(
//                BulkOperation.of(
//                        _1 -> _1.index(
//                                _2 -> _2.id("10")
//                                        .document(new Person("10", 10, "10")))));
//        bulkOperationArrayList.add(
//                BulkOperation.of(
//                        _1 -> _1.index(
//                                _2 -> _2.id("9")
//                                        .document(new Person("9", 9, "9")))));
//        bulkOperationArrayList.add(
//                BulkOperation.of(
//                        _1 -> _1.index(
//                                _2 -> _2.id("8")
//                                        .document(new Person("8", 8, "8")))));
//        bulkOperationArrayList.add(
//                BulkOperation.of(
//                        _1 -> _1.index(
//                                _2 -> _2.id("7")
//                                        .document(new Person("7", 7, "7")))));
//        BulkResponse bulkResponse = e_client.bulk(b -> b.index("test2")
//                .operations(bulkOperationArrayList));
//        logger.info("{}", bulkResponse.toString());
//        logger.info("{}", bulkResponse.errors());
//        logger.info("{}", bulkResponse.ingestTook());
//        for (BulkResponseItem item : bulkResponse.items()) {
//
//            logger.info("{}", item.id());
//            logger.info("{}", item.index());
//            logger.info("{}", item.result());
//            logger.info("{}", item.operationType());
//            logger.info("-----------------------------------");
//        }
    }

    // 更新文档 (全局更新)
    // POST /索引/_doc/{id}
    // 但不一样的是, 对应文档必须存在, 否则报错: ElasticsearchException: [es/update] failed: [document_missing_exception] [{id}]: document missing
    @Test
    void documentUpdateTest() throws IOException {
        UpdateResponse<Person> res = e_client.update(e ->
                        e.index("test2")
                                .id("13")
                                .doc(new Person("小洋人", 23, "Idol")),
                Person.class);
        logger.info("Res: {}", res.result().jsonValue());
        logger.info("{}", res.get());
        logger.info("{}", res);
    }

    // 删除文档
    // DELETE /索引/_doc/{id}
    @Test
    void documentDeleteTest() throws IOException {
        DeleteResponse res = e_client.delete(e
                -> e.index("test2")
                .id("12"));
        logger.info("Res: {}", res.result().jsonValue());
    }

    // 通过 ID 查询文档
    // GET /索引/_doc/{id}
    @Test
    void documentSearchIDTest() throws IOException {
        GetResponse<Person> res = e_client.get(e -> e.index("test2").id("11"), Person.class);
        logger.info("Res: {}", res.source());
        logger.info("{}", res.found());
        logger.info("{}", res.routing());
        logger.info("{}", res.toString());
    }

    // 索引主要 op
    @Test
    void indexOpTest() throws IOException {
        // get(本身 alias datastream indexTemplate template mapping setting)
        // put(alias datastream indexTemplate template mapping setting)
        // delete(本身 alias datastream indexTemplate template)
        // update(仅 aliases)
    }

    // 其他操作
    //多个值的 search
    // List 指定只有 key / value 的(单个)
    // Map 指定 key:value 的(键值对)
    // List & Map 都不能为空
    // 若为空则返回值也为空
    @Test
    void option_of_listAndMap() {
        // 索引名列表
        List<String> names = Arrays.asList("idx-a", "idx-b", "idx-c");

        // 为 foo & bar 准备基数聚合 (Cardinality Aggregation)
        Map<String, Aggregation> cardinalities = new HashMap<>();
        cardinalities.put("foo-count", Aggregation.of(a -> a.cardinality(c -> c.field("foo"))));
        cardinalities.put("bar-count", Aggregation.of(a -> a.cardinality(c -> c.field("bar"))));

        // 创建计算 size 的平均值的聚合(Aggregation)对象
        final Aggregation avgSize = Aggregation.of(a -> a.avg(v -> v.field("size")));

        SearchRequest search = SearchRequest.of(r -> r
                // Index list:
                // - add all elements of a list
                .index(names) // 直接使用 List 指定多个索引
                // - add a single element
                .index("idx-d") // 指定单个索引
                // - add a vararg list of elements
                .index("idx-e", "idx-f", "idx-g") // 将需要的索引全部列出

                // Sort order list: add elements defined by builder lambdas
                .sort(s -> s.field(f -> f.field("foo").order(SortOrder.Asc))) // 小到大排序
                .sort(s -> s.field(f -> f.field("bar").order(SortOrder.Desc))) // 大到小排序

                // Aggregation map:
                // - add all entries of an existing map
                .aggregations(cardinalities) // 指定定义好的聚合映射 (多个聚合对象)
                // - add a key/value entry
                .aggregations("avg-size", avgSize) // 指定定义好的聚合对象
                // - add a key/value defined by a builder lambda
                .aggregations("price-histogram",
                        a -> a.histogram(h -> h.field("price"))) // 临时定义聚合
        );

        // 区分 输入空值导致的空结果 和 非空输入值的空结果
        NodeStatistics stats = NodeStatistics.of(b -> b
                .total(1)
                .failed(0)
                .successful(1)
        );

        // 失败结果为空.
        // - 判断是否为空集
        assertNotNull(stats.failures());
        // - 判断结果是否为空
        assertEquals(0, stats.failures().size());
        // - 确定是否是输入参数为空
        assertFalse(ApiTypeHelper.isDefined(stats.failures()));
    }

    // 客户端类型
    // 阻塞式客户端
    // 异步式客户端 (所有的方法都返回一个标准的 CompletableFuture 对象)[线程相关]
    // 两者可同时使用, 共享一个传输(transport)对象
    @Test
    void clientType() throws IOException {
        if (e_client.exists(b -> b.index("products").id("foo")).value()) {
            logger.info("product exists");
        } else {
            logger.info("product does not exist");
        }

        ea_client
                .exists(b -> b.index("products").id("foo"))
                .thenAccept(response -> {
                    if (response.value()) {
                        logger.info("product exists");
                    } else {
                        logger.info("product does not exist");
                    }
                });
    }
}
