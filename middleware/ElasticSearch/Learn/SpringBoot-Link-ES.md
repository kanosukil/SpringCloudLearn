# SpringBoot 集成 ElasticSearch

## ES Ver. 8.1.2

### **Java API Client** 

#### 引入依赖

```xml
<!--依赖-->
<dependencies>        
    <!--Java API client-->
    <dependency>
        <groupId>co.elastic.clients</groupId>
        <artifactId>elasticsearch-java</artifactId>
        <version>8.1.2</version>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.12.3</version>
    </dependency>

    <!-- Java REST client -->
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>elasticsearch-rest-client</artifactId>
        <version>8.1.2</version>
    </dependency>

    <!-- Needed only if you use the spring-boot Maven plugin -->
    <dependency>
        <groupId>jakarta.json</groupId>
        <artifactId>jakarta.json-api</artifactId>
        <version>2.0.1</version>
    </dependency>
</dependencies>

<!--插件-->
<build>
    <plugins>
        <!--Java REST Client Plugins Configuration-->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <relocations>
                            <relocation>
                                <pattern>org.apache.http</pattern>
                                <shadedPattern>hidden.org.apache.http</shadedPattern>
                            </relocation>
                            <relocation>
                                <pattern>org.apache.logging</pattern>
                                <shadedPattern>hidden.org.apache.logging</shadedPattern>
                            </relocation>
                            <relocation>
                                <pattern>org.apache.commons.codec</pattern>
                                <shadedPattern>hidden.org.apache.commons.codec</shadedPattern>
                            </relocation>
                            <relocation>
                                <pattern>org.apache.commons.logging</pattern>
                                <shadedPattern>hidden.org.apache.commons.logging</shadedPattern>
                            </relocation>
                        </relocations>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

#### 配置 ES Client 进行连接

> [配置客户端 例子](E:\code\code\JavaWeb\SpringCloudLearn\middleware\ElasticSearch\ElasticSearch-Learn\src\main\java\learn\elasticsearch\config\ElasticSearchConnectConfig.java)

+ 创建 CredentialsProvider 对象 (ts: 凭证提供者, 即 设置账户密码)

> ```java
> final CredentialsProvider credentialsProvider 
>        = new BasicCredentialsProvider();
> credentialsProvider.setCredentials(AuthScope.ANY,
>                                       new UsernamePasswordCredentials(username, password));
> ```

+ 获得 Bean RestClient

> 通过 
>
> ```java
> RestClient.builder(
>        new HttpHost(hostname, port, scheme 协议)
>     .setHttpClientConfigCallback(httpAsyncClientBuilder
>                                     -> httpAsyncClientBuilder // lambda 表达式 参数可替换
>                                     .setDefaultCredentialsProvider(credentialsProvider))
>        .build();
> ```
>
> 若无需认证, 即直接 `return RestClient.builder(new HttpHost(hostname, port, scheme).build();` 即可

+ 通过 RestClient 获得 Bean ElasticsearchTransport

```java
new RestClientTransport(restClient, new JacksonJsonpMapper());
```

+ 通过 Transport 获得 Bean ElasticsearchClient (阻塞式客户端) 或者 Bean ElasticsearchAsyncClient (异步式客户端)

> 阻塞式客户端, 异步式客户端 (所有的方法都返回一个标准的 CompletableFuture 对象)[线程相关]; 两者可同时使用, 共享一个传输(transport)对象

```java
new ElasticsearchClient(elasticsearchTransport);
ElasticsearchAsyncClient(elasticsearchTransport);
```

#### 使用 客户端 对 ES 进行操作

> [ES操作 例子](E:\code\code\JavaWeb\SpringCloudLearn\middleware\ElasticSearch\ElasticSearch-Learn\src\test\java\learn\elasticsearch\ElasticSearchLearnTests.java)
>
> xxxRequest.of 预定义操作
>
> client 则是直接定义执行操作

+ 注入客户端

```java
@Resource
private ElasticsearchClient e_client;
@Resource
private ElasticsearchAsyncClient ea_client;
// 或者
@Autowired
@Qualifier("elasticsearchClient") // 指定 Bean 的方法名
private ElasticsearchClient e_client;
@Autowired
@Qualifier("elasticsearchAsyncClient") // 指定 Bean 的方法名
private ElasticsearchAsyncClient ea_client;
```

+ 直接使用客户端操作

```java
// e.g. search
// 实体的属性必须和文档的对应
SearchResponse<Person> search 
    = e_client.search(s -> s
                      .index("test1") // 索引
                      .query(q -> q // 搜索查询
                             .term(t -> t // term 精确查询
                                   .field("name") // .field("name.keyword") 可用
                                   .value(v -> v
                                          .stringValue("洋")) // 指定值
                                  )), Person.class);
// 创建了 Search 操作, 使用对应的 Response 对象接收后, 输出指定结果
for (Hit<Person> hit : search.hits().hits()) { // 遍历每一个匹配文档
    assert hit.source() != null;
    logger.info(hit.source().toString());
}
```

```java
// lambda 表达式的 参数使用 _N 表示 (深度级别)
// 每一个 build() 都可使用 lambda 替换
// 因此需要一眼即可识别的参数标识
CreateIndexResponse res 
    = e_client.indices().create(_1 -> _1
                                .index("test2")
                                .aliases("foo", 
                                         _2 -> _2
                                         .isWriteIndex(true)));
/*
上句 相当于
CreateIndexResponse createResponse = client.indices().create(
	new CreateIndexRequest.Builder()
		.index("test2")
		.aliases("foo",
			new Alias.Builder().isWriteIndex(true).build()
).build());
*/
```

> 特征: 
>
> 1. 使用 List & Map 多值操作
>
>    + List 将 多个 value 操作
>    + Map 将 多个 key:value 操作
>
>    > 注意: List & Map 不能有 空值(空值返回值也是空值)
>    >
>    > 区分: 输入空值返回空值 和 输入非空值返回空值
>    >
>    > 借助 **ApiTypeHelper** 类
>    >
>    > ```java
>    > // 区分 输入空值导致的空结果 和 非空输入值的空结果
>    > NodeStatistics stats = NodeStatistics.of (
>    >        b -> b
>    >        .total(1)
>    >        .failed(0)
>    >        .successful(1)
>    > );
>    > 
>    > // 失败结果为空.
>    > // - 判断是否为空集
>    > assertNotNull(stats.failures());
>    > // - 判断结果是否为空
>    > assertEquals(0, stats.failures().size());
>    > // - 确定是否是输入参数为空
>    > assertFalse(ApiTypeHelper.isDefined(stats.failures()));
>    > ```
>    >
>    > 
>
> 2. 

#### 使用 Json 格式的数据操作 ES

+ 创建 .json 文件

```java
//POST /索引(index)/_doc
//{ json 数据(以文件的形式) }

// 方法1: 
InputStream input 
    = this.getClass()
    .getResourceAsStream("some-index.json"); // 从文件中获得 Json 数据
// 创建索引请求
CreateIndexRequest req 
    = CreateIndexRequest.of(b -> b
                            .index("some-index") // 设置目标索引
                            .withJson(input) // 作为请求体参数传入
                           );
// 执行请求
boolean created 
    = client.indices().create(req).acknowledged();
// 获得是否创建成功的结果

// 方法2:
FileReader file = new FileReader(new File(dataDir, "document-1.json")); // 读取文件 获取 json 数据
IndexRequest<JsonData> req; // 索引请求
req = IndexRequest.of(b -> b
                      .index("some-index")
                      .withJson(file) // json 数据作为请求体
                     );
// 执行请求
client.index(req);
```

+ 直接编写 json 格式的字符串

```java
// 单个 Json 数据传入查询
Reader queryJson = new StringReader(
    "{" +
    "  \"query\": {" +
    "    \"range\": {" +
    "      \"@timestamp\": {" +
    "        \"gt\": \"now-1w\"" +
    "      }" +
    "    }" +
    "  }" +
    "}");
// 以字符串的形式写 Json 数据
SearchRequest aggRequest 
    = SearchRequest.of(b -> b
                       .withJson(queryJson) // 传入 Reader 转换的 Json 数据
                       .aggregations("max-cpu", a1 -> a1 // 聚合查询
                                     .dateHistogram(h -> h
                                                    .field("@timestamp")
                                                    .calendarInterval(CalendarInterval.Hour)
                                                   )
                                     .aggregations("max", a2 -> a2 // 嵌套聚合查询
                                                   .max(m -> m.field("host.cpu.usage"))
                                                  )
                                    )
                       .size(0)
                      );
Map<String, Aggregate> aggs = client
    .search(aggRequest, Void.class) 
    .aggregations(); // 获取聚合查询的结果
```

```java
// 编写多个 Json 格式数据进行 ES 操作
Reader queryJson = new StringReader(
    "{" +
    "  \"query\": {" +
    "    \"range\": {" +
    "      \"@timestamp\": {" +
    "        \"gt\": \"now-1w\"" +
    "      }" +
    "    }" +
    "  }," +
    "  \"size\": 100" + 
    "}");
// 查询操作 Json
Reader aggregationJson = new StringReader(
    "{" +
    "  \"size\": 0, " + 
    "  \"aggregations\": {" +
    "    \"hours\": {" +
    "      \"date_histogram\": {" +
    "        \"field\": \"@timestamp\"," +
    "        \"interval\": \"hour\"" +
    "      }," +
    "      \"aggregations\": {" +
    "        \"max-cpu\": {" +
    "          \"max\": {" +
    "            \"field\": \"host.cpu.usage\"" +
    "          }" +
    "        }" +
    "      }" +
    "    }" +
    "  }" +
    "}");
// 聚合操作 Json 
SearchRequest aggRequest 
    = SearchRequest.of(b -> b
                       .withJson(queryJson) // 传入 查询操作
                       .withJson(aggregationJson)  // 传入 聚合操作
                       .ignoreUnavailable(true)  // 忽略失败结果
                      );
Map<String, Aggregate> aggs = client
    .search(aggRequest, Void.class)
    .aggregations(); // 执行并获取操作结果
```

#### 索引

```java
// 创建 返回: CreateIndexResponse
client.indices()
    .create(_1 -> _1
            .index("test2") // 索引名
            .aliases("foo", // 别名 可选
                     _2 -> _2
                     .isWriteIndex(true))
           );
// 删除 返回: DeleteIndexResponse
client.indices()
    .delete(d -> d
            .index("test2"));
// 查询 (不知道输出数据的方法) 返回: GetIndexResponse
client.indices()
    .get(e -> e
         .index("test1"));
// 判断是否存在 返回: BooleanResponse
client.indices()
    .exists(e -> e
            .index("test1"));
```

#### 文档

> 需要事先创建好对应的 实体类 (无参构造方法必须存在)

```java
// 创建 返回: IndexResponse
client.index(e -> e
             .index("test1")
             .id("1020")
             .document(
                 new Person("洋洋", 22, "student")
             ));
// 批量创建: 利用 bulk
List<FileSearch> fileSearchList 
    = new ArrayList<>(); // 范式为 自己创建的文档对应的类
List<BulkOperation> bulkOperationArrayList 
    = new ArrayList<>(); // 范式为 BulkOperation 类
//遍历添加到bulk中
for(FileSearch fileSearch : fileSearchList){
    bulkOperationArrayList.add(
        BulkOperation.of(o->o
                         .index(i->i
                                .document(fileSearch))));
}
BulkResponse bulkResponse 
    = client.bulk(b -> b
                  .index("filesearch")
                  .operations(bulkOperationArrayList)); // 执行并获得结果

// 更新 返回: UpdateResponse<Person> // Person 为对应的实体类
// 个人理解: client.update(options, class);
client.update(e -> e
              .index("test1")
              .id("1020")
              .doc(new Person("小洋人", 23, "Idol")),
              Person.class);

// 删除 返回: DeleteResponse
client.delete(e -> e
              .index("test1")
              .id("1020"));

// 通过 id 获取文档 返回: GetResponse<Person> // Person w
// 个人理解: client.get(options, class);
client.get(e -> e
           .index("test1")
           .id("1001"),
           Person.class);

// 判断文档是否存在 返回: BooleanResponse
client.exists(e -> e
              .index("test1")
              .id("1001"));
```

#### 查询

```java
SearchResponse<Person> complex 
    = e_client.search(s -> s
                      .index("test1").query(q -> q
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
                                                              .value("novelist"))))),
                      Person.class);
// bool 布尔查询 以及 filter 过滤器(?) 
// must / should / mustNot
// ElasticSearch 的各项操作基本上都可以运行
// highlight sort aggregation ...
```

+ 检索方式

  1. term: 不分词检索

  2. match: 分词检索

  3. wildcard:  通配符检索 (英文的分词为分单词, 因此需要 wildcard 进行对单词的分)

     + \'?\' : 匹配一个字符

     + \'\*\' : 匹配0个或多个字符


+ 分页

>  `query().from().size()`
>
> 两者顺序没有要求

+ 高亮

```java
.query(...)
    .highlight(h -> h
               .fields("name", f -> f
                       .type("plain") // 有固定值 未知 (String 类型) 非必须 但错了就报错
                       .preTags("<a id=\"highlight\">")
                       .postTags("</a>"))
               .encoder(HighlighterEncoder.Html)) // 非必须
```

## ES Ver. 7.17.2

> ### Compatibility mode: using a 7.17 client with Elasticsearch 8.x
>
> The HLRC version `7.17` can be used with Elasticsearch version `8.x` by enabling HLRC’s compatibility mode (see code sample below). In this mode HLRC sends additional headers that instruct Elasticsearch `8.x` to behave like a `7.x` server.
>
> The Java API Client doesn’t need this setting as compatibility mode is always enabled.
>
> > 译文: 
> >
> > 兼容性模式：在Elasticsearch 8.x中使用7.17客户端
> > 通过启用HLRC的兼容模式，HLRC 7.17版本可以与Elasticsearch 8.x版本一起使用（见下面的代码示例）。在这种模式下，HLRC会发送额外的头信息，指示Elasticsearch 8.x像7.x服务器那样行事。
> >
> > Java API客户端不需要这个设置，因为兼容模式总是被启用。

### 引入依赖

+ Spring Initializr 配置

+ maven pom.xml 配置

  ```xml
  <dependency>
      <groupId>org.elasticsearch.client</groupId>
      <artifactId>elasticsearch-rest-high-level-client</artifactId>
      <version>7.17.2</version>
  </dependency>
  ```

+ 修改上面两个依赖

  > 上面的依赖不是 org.elasticsearch.client:elasticsearch-rest-client 的版本和 org.elasticsearch.client:elasticsearch-rest-high-level-client 对不上, 就是  org.elasticsearch:elasticsearch 版本对不上, 因此需要同一版本.

  ```xml
  <!--Java High Level REST Client-->
  <dependency>
      <groupId>org.elasticsearch.client</groupId>
      <artifactId>elasticsearch-rest-high-level-client</artifactId>
      <version>7.17.2</version>
      <exclusions>
          <exclusion>
              <groupId>org.elasticsearch.client</groupId>
              <artifactId>elasticsearch-rest-client</artifactId>
          </exclusion>
          <exclusion>
              <groupId>org.elasticsearch</groupId>
              <artifactId>elasticsearch</artifactId>
          </exclusion>
      </exclusions>
  </dependency>
  
  <dependency>
      <groupId>org.elasticsearch.client</groupId>
      <artifactId>elasticsearch-rest-client</artifactId>
      <version>7.17.2</version>
  </dependency>
  
  <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>elasticsearch</artifactId>
      <version>7.17.2</version>
  </dependency>
  ```

### 配置链接

```java
// Create the REST client
RestClient httpClient = RestClient.builder(
    new HttpHost("localhost", 9200)
).build();

// Create the HLRC
RestHighLevelClient hlrc = new RestHighLevelClientBuilder(httpClient)
    .setApiCompatibilityMode(true) // 开启兼容模式 必须开启, 不然报错 
    .build();
```

> Enables compatibility mode that allows HLRC `7.17` to work with Elasticsearch `8.x`.
>
> 启用兼容模式，允许HLRC `7.17`与Elasticsearch `8.x`一起工作。

### 操作 ElasticSearch

> <a id="example"> [示例](E:\code\code\JavaWeb\SpringCloudLearn\middleware\ElasticSearch\ElasticSearch-Learn\HighLevelRESTClient_Version7.17.2\src\test\java\learn\elasticsearch\HighLvClientTests.java) </a>
>
> 参考: [【ES】【Java High Level REST Client】官方索引和文档操作指导 - 风动静泉 - 博客园 (cnblogs.com)](https://www.cnblogs.com/z00377750/p/13300196.html)
>
> 官网: [`Document APIs | Java REST Client [7.17] | Elastic`](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-supported-apis.html)

#### 索引

+ 创建

  1. 创建请求: `CreateIndexRequest`

  2. 执行请求: `client.indices().create(CreateIndexRequest, RequestOptions) // 返回 CreateIndexResponse`   
  3. 处理响应

+ 获得/判断存在

  1. 创建请求: `GetIndexRequest`

  2. 执行请求: 

     `client.indices().exists(GetIndexRequest, RequestOptions) // 返回 boolean 判断是否存在`

     `client.indices().get(GetIndexRequest, RequestOptions) // 返回 GetIndexResponse 可以处理返回信息`  

  3. 处理响应

+ 删除

  1. 创建请求: `DeleteIndexRequest`
  2. 执行请求: `client.indices().delete(DeleteIndexRequest, RequestOptions) // 返回 AcknowledgedResponse`
  3. 处理响应

#### 文档

##### 创建`IndexRequest`

+ 创建文档对象 -> 创建 IndexRequest 请求对象 -> 执行请求 -> 处理响应

  > 1. 类对象:
  >
  >    ```java
  >    // 创建文档
  >    Person person = new Person("巴姆彬鸥", 3, null);
  >    // 创建请求 并配置请求 绑定文档
  >    IndexRequest request = new IndexRequest("test2");
  >    request.id("1030");
  >    request.timeout("10s"); // = request.timeout(TimeValue.timeValueSeconds(10));
  >    // 必须将对象转换成 Json
  >    request.source(mapper.writeValueAsString(person), XContentType.JSON);
  >    ```
  >
  > 2. JsonString:
  >
  >    ```java
  >    // 创建请求 并配置请求
  >    IndexRequest request = new IndexRequest("test2");
  >    request.id("1031");
  >    // 创建文档
  >    String json =
  >        "{" +
  >            "\"name\": \"刘乐\"," +
  >            "\"age\": 42," +
  >            "\"job\": \"Teacher\"" +
  >        "}";
  >    // 绑定文档
  >    request.source(json, XContentType.JSON);
  >    ```
  >
  > 3. Map:
  >
  >    ```java
  >    // 创建文档
  >    Map<String, Object> jsonMap = new HashMap<>();
  >    jsonMap.put("name", "陈天乐");
  >    jsonMap.put("age", 23);
  >    jsonMap.put("job", "Cleaner");
  >    // 创建请求 并配置请求 绑定文档
  >    IndexRequest request = new IndexRequest("test2")
  >            .id("1032")
  >            .source(jsonMap);
  >    ```
  >
  > 4. XContentBuilder:
  >
  >    ```java
  >    // 创建文档
  >    XContentBuilder builder = XContentFactory.jsonBuilder();
  >    builder.startObject();
  >    {
  >        builder.field("name", "张乐轩");
  >        builder.field("age", 39);
  >        builder.field("job", "Teacher");
  >        builder.timeField("createDate", new Date());
  >    }
  >    builder.endObject();
  >    // 创建请求 并配置请求 绑定文档
  >    IndexRequest request = new IndexRequest("test2")
  >            .id("1033")
  >            .source(builder);
  >    ```
  >
  > 5. Key-Pairs:
  >
  >    ```java
  >    // 创建请求 并配置请求 在 请求上定义文档
  >    IndexRequest request = new IndexRequest("test2")
  >            .id("1034")
  >            .source("name", "周张陈",
  >                    "age", 26,
  >                    "job", "Programmer");
  >    ```
  >
  
  + 可选请求配置
  
    ```java
    request.routing("routing"); // 路由参数
    
    request.timeout(TimeValue.timeValueSeconds(1)); // 以 TimeValue 形式设置主分片超时时间
    request.timeout("1s"); // 以String形式设置主分片超时时间
    
    request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL); // 使用 WriteRequest.RefreshPolicy 实例设置刷新策略
    request.setRefreshPolicy("wait_for"); // 使用 String 设置刷新策略
    
    request.version(2); // 设置 version
    request.versionType(VersionType.EXTERNAL); // 设置 version type
    
    request.opType(DocWriteRequest.OpType.CREATE); // 使用 DocWriteRequest.OpType 值设置操作类型
    request.opType("create"); // 使用 String 设置操作类型
    
    request.setPipeline("pipeline"); // 请求执行前需要执行的 ingest pipeline
    ```
  
  + 执行请求 获得响应
  
    ```java
    // 异步执行 listener 泛型为 IndexResponse
    client.indexAsync(request, RequestOptions.DEFAULT, listener);
    client.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
        @Override
        public void onResponse(IndexResponse indexResponse) {
            // 成功获得响应时的操作
        }
    
        @Override
        public void onFailure(Exception e) {
            // 获得失败响应时的操作
        }
    });
    // 阻塞式
    IndexResponse response = client.index(request, RequestOptions.DEFAULT);
    ```
  
  + 处理响应
  
    ```java
    IndexRequest request ... // request 配置
    // 执行请求
    try {
        IndexResponse response = client.index(request, RequestOptions); // RequestOptions 可自行配置
    } catch(ElasticsearchException e) {
        if (e.status() == RestStatus.CONFLICT) {
            // 1. 此处说明抛出了版本冲突异常
            // 2. 此处说明抛出 opType 设为 create 时, 对应 id 已有文档时的异常
        }
    }
    String index = indexResponse.getIndex();
    String id = indexResponse.getId();
    if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
        // 首次创建文档的处理
    } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
        // 已经存在的文档的更新
    }
    ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
    if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
        // 执行成功的分片数少于总分片数时，在此处处理
    }
    if (shardInfo.getFailed() > 0) {
        for (ReplicationResponse.ShardInfo.Failure failure :
                shardInfo.getFailures()) {
            // 失败处理
            String reason = failure.reason(); 
        }
    }
    ```

##### 更新 `UpdateRequest`

+ 创建请求

   ```java
   UpdateRequest request = new UpdateRequest(String 索引名, String id);
   ```

+ 创建更新的文档对象

   > 1. 类对象 
   >
   >    ```java
   >    request.doc(将对象转换成 JSON String 格式, XContentType.JSON); // Jackson 或 Fastjson
   >    ```
   >
   >    使用 requset 的 doc 方法
   >
   > 2. Script 脚本对象
   >
   >    ```java
   >    // 脚本参数
   >    Map<String, Object> parameters = singletonMap("count", 4); 
   >    // 内联(inline)脚本: painless 语言 + 参数
   >    Script inline = new Script(
   >        ScriptType.INLINE, "painless", "ctx._source.field += params.count", parameters);  
   >    // 将脚本加入请求
   >    request.script(inline);  
   >    ```
   >
   > 3. Stored Script 对象
   >
   >    ```java
   >    // 脚本参数
   >    Map<String, Object> parameters = singletonMap("count", 4); 
   >    // 引用 painless 语言内置的脚本
   >    Script stored = new Script(
   >        ScriptType.STORED, null, "increment-field", parameters);  
   >    // 将脚本加入请求
   >    request.script(stored);  
   >    ```
   >
   >     ***下四个跟创建文档的一样, 只是方法 从 source 变为 doc***, 效果: 文档部分更新 (非覆盖)
   >
   > 4. JsonString
   >
   > 5. Map
   >
   > 6. XContentBuilder
   >
   > 7. Key-Pairs

+ 需更新的文档不存在时的处理

   > 更新的文档不存在, 则创建文档

   ```java
   request.docAsUpsert(true); // 设置如果要更新的文档不存在，则文档变为 upsert 文档
   request.upsert(jsonString, XContentType.JSON); // 参数和 doc 方法一样 可使用 JsonString Map XContentBuilder Key-Pairs & Object
   ```

+ 可选请求配置

   ```java
   request.routing("routing"); // 路由参数
   
   request.timeout(TimeValue.timeValueMinutes(2)); // 以TimeValue形式设置主分片超时时间
   request.timeout("2m"); // 以String形式设置主分片超时时间
   
   
   request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL); // 使用 WriteRequest.RefreshPolicy 实例设置刷新策略
   request.setRefreshPolicy("wait_for"); // 使用 String 设置刷新策略
   
   request.version(2); // 设置 version
   request.versionType(VersionType.EXTERNAL); // 设置version type
   
   request.retryOnConflict(3); // 如果在执行更新时已经被其他操作修改，重新尝试的次数设置
   
   request.fetchSource(true); // 开启获取_source字段，默认关闭
   
   String[] includes = new String[]{"updated", "r*"}; // 配置 source 包含的具体字段
   String[] excludes = new String[]{"updated"}; // 配置source排除的具体字段
   request.fetchSource(new FetchSourceContext(true, includes, excludes));
   
   request.setIfSeqNo(2L); // ifSeqNo
   request.setIfPrimaryTerm(1L); // ifPrimaryTerm
   
   request.detectNoop(false); // 关闭 noop 探测
   
   request.scriptedUpsert(true); // 设置不管文档是否存在，脚本都被执行
   
   request.waitForActiveShards(2); // 设置更新操作执行前活动的分片副本数量
   request.waitForActiveShards(ActiveShardCount.ALL); // 可以作为活跃分区副本的数量 ActiveShardCount：取值为 ActiveShardCount.ALL, ActiveShardCount.ONE 或者  ActiveShardCount.DEFAULT (默认值)
   ```

+ 执行请求 获得响应

   ```java
   // 阻塞式
   UpdateResponse updateResponse = client.update(request, RequestOptions);
   // 异步。listener 的泛型为 UpdateResponse
   client.updateAsync(request, RequestOptions, listener);
   client.updateAsync(request, RequestOptions.DEFAULT, new ActionListener<UpdateResponse>() {
       @Override
       public void onResponse(UpdateResponse updateResponse) {
           // 成功响应处理
       }
   
       @Override
       public void onFailure(Exception e) {
           // 失败响应处理
       }
   })
   ```

+ 处理响应

  ```java
  UpdateRequest request ... // 配置请求
  try {
      UpdateResponse updateResponse = client.update(request, RequestOptions); // RequestOption 可自行配置
  } catch (ElasticsearchException e) {
      if (e.status() == RestStatus.NOT_FOUND) {
          // 处理由于文档不存在导致的异常
      }
      if (e.status() == RestStatus.CONFLICT) {
          // 版本冲突导致异常
      }
  }
  String index = updateResponse.getIndex();
  String id = updateResponse.getId();
  long version = updateResponse.getVersion();
  if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
      // 首次创建或upsert
  } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
      // 文档被更新
  } else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
      // 文档被删除
  } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
      // 未对已有文档造成影响
  }
  // 以 GetResult 对象获取被更新的文档
  GetResult result = updateResponse.getGetResult(); 
  if (result.isExists()) {
      // 以String形式获取被更新文档的 source
      String sourceAsString = result.sourceAsString(); 
      // 以 Map<String, Object> 形式获取被更新文档的 source
      Map<String, Object> sourceAsMap = result.sourceAsMap(); 
      // 以 byte[] 形式获取被更新文档的 source
      byte[] sourceAsBytes = result.source(); 
  } else {
      // 处理响应中没有 source 的情形（默认行为）
  }
  ReplicationResponse.ShardInfo shardInfo = updateResponse.getShardInfo();
  if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
      // 执行成功的分片数少于总分片数时，在此处处理
  }
  if (shardInfo.getFailed() > 0) {
      for (ReplicationResponse.ShardInfo.Failure failure :
              shardInfo.getFailures()) {
          // 处理失败信息
          String reason = failure.reason(); 
      }
  }
  ```

##### 获取 `GetRequest`

+ 创建请求 -> 执行请求

   1. Exists

   ```java
   GetRequest getRequest = new GetRequest(String 索引名, String id); // 设置请求的索引和文档ID
   getRequest.fetchSourceContext(new FetchSourceContext(false)); // 关闭获取 _source字段
   getRequest.storedFields("_none_"); // 关闭获取 stored fields
   
   // 阻塞式
   boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
   // 异步，listener 泛型为 Boolean
   client.existsAsync(getRequest, RequestOptions.DEFAULT, listener);
   client.existsAsync(request, RequestOptions.DEFAULT, new ActionListener<Boolean>() {
       @Override
       public void onResponse(Boolean aBoolean) {
           // 响应成功时的操作
       }
   
       @Override
       public void onFailure(Exception e) {
           // 获得失败响应时的操作
       }
   });
   ```

   2. Get & GetSource

      > GetRequest，可以使用 SearchRequest 取代
      >
      > GetSourceRequest，可以使用 SearchRequest 中的 SearchSourceBuilder 的 fetchSource 方法取代
      >
      > <a href="#example">示例</a>

   3. 

##### 删除 `DeleteRequest`

+ 创建请求

   ```java
   DeleteRequest request = new DeleteRequest(String 索引名, String id);
   // 可选配置
   request.routing("routing"); // 路由参数
   
   request.timeout(TimeValue.timeValueMinutes(2)); // 以 TimeValue 形式设置主分片超时时间
   request.timeout("2m"); // 以 String 形式设置主分片超时时间
   
   request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL); // 使用WriteRequest.RefreshPolicy 实例设置刷新策略
   request.setRefreshPolicy("wait_for"); // 使用String设置刷新策略
   
   request.version(2); // 设置version
   request.versionType(VersionType.EXTERNAL); // 设置version type
   ```

+ 执行请求 获得响应

  ```java
  // 阻塞式
  DeleteResponse deleteResponse = client.delete(request, RequestOptions);
  // 异步， listener 泛型为 DeleteResponse
  client.deleteAsync(request, RequestOptions, listener); 
  client.deleteAsync(request, RequestOptions, new ActionListener<DeleteResponse>() {
      @Override
      public void onResponse(DeleteResponse deleteResponse) {
          // 响应成功时的操作
      }
  
      @Override
      public void onFailure(Exception e) {
          // 获得失败响应时的操作
      }
  });
  ```

+ 处理响应

  ```java
  DeleteRequest request ... // 配置请求
  // 执行请求
  try {
      DeleteResponse deleteResponse = client.delete(request, RequestOptions); // RequestOptions 可执行配置
  } catch (ElasticsearchException exception) {
      if (exception.status() == RestStatus.CONFLICT) {
          // 版本冲突异常处理
      }
  }
  String index = deleteResponse.getIndex();
  String id = deleteResponse.getId();
  long version = deleteResponse.getVersion();
  if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
      // 没找到文档，执行相应处理
  }
  ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
  if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
       // 执行成功的分片数少于总分片数时，在此处处理
  }
  if (shardInfo.getFailed() > 0) {
      for (ReplicationResponse.ShardInfo.Failure failure :
              shardInfo.getFailures()) {
          // 失败处理
          String reason = failure.reason(); 
      }
  }
  ```

##### Bulk 批量处理

> 实际项目中 常用批量处理

###### BulkRequest & BulkResponse 

> 执行多个 index / update / delete 操作, 但至少需要一个操作
>
> **注意，Bulk API 只支持JSON或SMILE编码格式，使用其他格式的文档会报错。**

+ 创建请求

    ```java
    // 1 
    BulkRequest defaulted = new BulkRequest(String 索引名); // 全局索引，适用于所有子请求，除非子请求单独设置了索引。该参数是 @Nullable，且只有在 BulkRequest 创建时设定。
    // 2
    BulkRequest request = new BulkRequest();
    // 添加删除文档操作请求DeleteRequest
    request.add(new DeleteRequest(String 索引名, String id)); 
    // 添加更新文档操作请求UpdateRequest
    request.add(new UpdateRequest(String 索引名, String id) 
            .doc(XContentType.JSON,"other", "test"));
    // 添加创建文档操作请求IndexRequst，使用SMILE格式
    request.add(new IndexRequest(String 索引名).id(, String id)  
            .source(XContentType.JSON,"field", "baz"));
    ```

+ 可选请求配置

   ```java
   request.timeout(TimeValue.timeValueSeconds(1)); // 以TimeValue形式设置主分片超时时间
   request.timeout("1s"); // 以String形式设置主分片超时时间
   
   request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL); // 使用 WriteRequest.RefreshPolicy 实例设置刷新策略
   request.setRefreshPolicy("wait_for"); // 使用String设置刷新策略
   
   request.version(2); // 设置 version
   request.versionType(VersionType.EXTERNAL); // 设置 version type
   
   request.setPipeline("pipelineId"); // 全局 pipeline，适用于所有子请求，除非子请求覆写了 pipeline
   
   request.waitForActiveShards(2); // 设置index/update/delete操作执行前活动的分片副本数量
   request.waitForActiveShards(ActiveShardCount.ALL); // 可以作为活跃分区副本的数量ActiveShardCount：取值为 ActiveShardCount.ALL, ActiveShardCount.ONE 或者  ActiveShardCount.DEFAULT (默认值)
   
   request.routing("routingId"); // 设置全局路由，适用于所有子请求
   ```

+ 执行请求 获取响应

    ```java
    // 阻塞式
    BulkResponse bulkResponse = client.bulk(request, RequestOptions);
    // 异步。listener 泛型 BulkResponse
    client.bulkAsync(request, RequestOptions, listener);
    client.bulkAsync(request, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
        @Override
        public void onResponse(BulkResponse bulkItemResponses) {
            // 成功响应的操作
        }
        @Override
        public void onFailure(Exception e) {
            // 失败响应的操作
        }
    })
    ```

+ 处理响应

  > 遍历所有响应/迭代获取响应

  ```java
  for (BulkItemResponse bulkItemResponse : bulkResponse) { 
      // 检索操作的响应(无论成功与否)，可以是IndexResponse、UpdateResponse 或 DeleteResponse (都是 DocWriteResponse 实例)。
      DocWriteResponse itemResponse = bulkItemResponse.getResponse(); 
      // 根据 响应类型呢处理
      switch (bulkItemResponse.getOpType()) {
      case INDEX:    // 处理 index/create 响应
      case CREATE:   
          IndexResponse indexResponse = (IndexResponse) itemResponse;
          break;
      case UPDATE:   // 处理 update 响应
          UpdateResponse updateResponse = (UpdateResponse) itemResponse;
          break;
      case DELETE:   // 处理 delete 响应
          DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
      }
  }
  ```

  > 检查是否有失败响应

  ```java
  // 至少有一个执行失败时，返回true
  if (bulkResponse.hasFailures()) { 
      // 有失败就遍历获取错误的响应
      for (BulkItemResponse bulkItemResponse : bulkResponse) {
          // 判断操作是否失败
          if (bulkItemResponse.isFailed()) { 
              // 如果失败，则获取失败信息
              BulkItemResponse.Failure failure =
                  bulkItemResponse.getFailure(); 
              // 相应的处理
          }
      }
  }
  ```

###### Bulk Processor

> 简化 Bulk 操作, 允许 index/update/delete 操作添加到 Processor 中后, 透明执行.
>
> BulkProcessor: 
>
> + RestHighLevelClient : 执行请求(BulkRequest) 获得结果(BulkResponse)
> + BulkProcessor.Listener : 请求执行失败时或执行完成后调用 Listener
> + BulkProcessor.builder() : 创建 BulkProcessor

```java
// 创建 BulkProcessor.Listener
BulkProcessor.Listener listener = new BulkProcessor.Listener() { 
    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
        // 每个 BulkRequest 执行前调用
        // e.g. 执行前获取 Request 数量
        int numberOfActions = request.numberOfActions(); 
        logger.debug("Executing bulk [{}] with {} requests",
                executionId, numberOfActions);
    }
    @Override
    public void afterBulk(long executionId, BulkRequest request,
            BulkResponse response) {
        // 每个 BulkRequest 执行完成后 调用
        // e.g. 判断执行完成后 是否有执行失败的请求
        if (response.hasFailures()) { 
            logger.warn("Bulk [{}] executed with failures", executionId);
        } else {
            logger.debug("Bulk [{}] completed in {} milliseconds",
                    executionId, response.getTook().getMillis());
        }
    }
    @Override
    public void afterBulk(long executionId, BulkRequest request,
            Throwable failure) {
        // 每个 BulkRequest 执行失败后 调用
        // e.g. 获取 BulkRequest 执行失败的原因
        logger.error("Failed to execute bulk", failure); 
    }
};

//创建 BulkProcessor
BulkProcessor bulkProcessor = BulkProcessor.builder(
        (request, bulkListener) ->
            client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
        listener).build();
```
> 配置 BulkProcessor

```java
// 根据请求的数量, 设置何时 flush 的请求 (默认为 1000，使用 -1 禁用)
builder.setBulkActions(500); 
// 根据请求的总容量大小, 设置何时 flush 的请求 (默认为 5MB，使用 -1 禁用)
builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB)); 
// 设置并发请求数(默认为 1, 使用 0 表示只允许执行一个请求)
builder.setConcurrentRequests(0); 
// 设置 flush 间隔 (默认 关闭)
builder.setFlushInterval(TimeValue.timeValueSeconds(10L)); 
// 设置回退机制(?) 下例: 初始化为 等待1秒, 重试3次
// BackoffPolicy.noBackoff() 关闭
// BackoffPolicy.constantBackoff() 间隔时间 按常数固定
// BackoffPolicy.exponentialBackoff() 间隔时间 按照指数式增长
builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3)); 
```

> 向 BulkProcessor 中添加 Request

```java
bulkProcessor.add(new IndexRequest);
bulkProcessor.add(new UpdateRequest);
bulkProcessor.add(new DeleteRequest);
```

> 关闭 BulkProcessor
>
> **注**: 下述两个关闭方法会在关闭前刷新 processor 中已经添加的请求, 且无法向 procssor 中添加新的请求.

```java
// Method 1: 
// 如果所有请求执行完成返回 true，如果请求执行超时，则返回 false
boolean terminated = bulkProcessor.awaitClose(30L, TimeUnit.SECONDS);

// Method 2:
bulkProcessor.close();
```

##### UpdateByQueryRequest & DeleteByQueryRequest

> 更新一个索引中的多个文档 & 删除一个索引中的多个文档

+ 简单示例

  + 创建

  ```java
  // 在一组索引上创建 UpdateByQueryRequest
  // UpdateByQueryRequest
  UpdateByQueryRequest request = new UpdateByQueryRequest("source1", "source2"); 
  // DeleteByQueryRequest
  DeleteByQueryRequest request = new DeleteByQueryRequest("source1", "source2"); 
  ```

  + 配置

    > 更新和删除的配置类似 因此下面的 request 两个都代表

  ```java
  request.setConflicts("proceed");// 只进行计数, 以避免版本冲突导致的 request 请求执行中断
  
  request.setQuery(new TermQueryBuilder("user", "kimchy")); // 只处理user字段值为kimchy的文档
  
  request.setMaxDocs(10); // 限制处理文档的最大数量
  request.setBatchSize(100); // 修改 request 一批处理的文档数量
  
  request.setSlices(2); // 通过setSlices使用sliced-scroll实现并行化
  
  request.setScroll(TimeValue.timeValueMinutes(10));  // 通过 scroll 参数设置 search context 的生命周期
  
  request.setRouting("=cat");  // 设置路由, 用以限制对应分片
  
  request.setTimeout(TimeValue.timeValueMinutes(2));  // 批量处理的超时时间
  request.setRefresh(true);  // 处理后是否 refresh
  request.setIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN); // 索引选项
  ```

  > UpdateByQueryRequest 特有:
  >
  > ```java
  > request.setPipeline("my_pipeline"); // 指定 pipeline
  > 
  > // 使用脚本更新
  > // setScript 增加用户为 kimchy 的所有文档的likes 字段值
  > request.setScript( new Script(
  >         ScriptType.INLINE, "painless",
  >         "if (ctx._source.user == 'kimchy') {ctx._source.likes++;}",
  >         Collections.emptyMap()));
  > ```
  >
  > 

  + 执行请求 获取响应

  ```java
  // 阻塞式
  BulkByScrollResponse bulkResponse =
          client.deleteByQuery(request, RequestOptions.DEFAULT);
  // 异步， listener 泛型 BulkByScrollResponse
  client.deleteByQueryAsync(request, RequestOptions.DEFAULT, listener);
  ```

  + 处理响应

  ```java
  TimeValue timeTaken = bulkResponse.getTook(); // 所有请求执行的总耗时
  boolean timedOut = bulkResponse.isTimedOut(); // 是否超时
  long totalDocs = bulkResponse.getTotal(); // 获取所有文档的数量
  long updatedDocs = bulkResponse.getUpdated(); // 获取执行更新的文档数量
  long deletedDocs = bulkResponse.getDeleted(); // 获取执行删除的文档数量
  long batches = bulkResponse.getBatches(); // 获取 批处理 的数量
  long noops = bulkResponse.getNoops(); // 获取跳过执行的文档数量
  long versionConflicts = bulkResponse.getVersionConflicts(); // 获取版本冲突的数量
  long bulkRetries = bulkResponse.getBulkRetries(); // 获取重试 bulk 操作的数量
  long searchRetries = bulkResponse.getSearchRetries(); // 获得重新搜索的 请求数量
  
  TimeValue throttledMillis =
      bulkResponse.getStatus().getThrottled(); // throttle 节流(?)
  TimeValue throttledUntilMillis = 
      bulkResponse.getStatus().getThrottledUntil(); // 
  List<ScrollableHitSource.SearchFailure> searchFailures =
      bulkResponse.getSearchFailures(); // 失败的 搜索 请求
  List<BulkItemResponse.Failure> bulkFailures =
      bulkResponse.getBulkFailures(); // 失败的 bulk index 请求
  ```


#### 查询

##### Search API

+ Search Request

  > 创建请求 SearchRequest
  >
  > 查询的请求体需要使用 SearchSourceBuilder 配置

  ```java
  SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); // 创建 SearchSourceBuilder 
  // 配置请求体 SearchSourceBuilder
  sourceBuilder.query(QueryBuilders.termQuery("name", "洋"))
      // QueryBuilders 工具类, 内置许多默认 QueryBuilder
      // 可以自己配置 QueryBuilder 的实现类 或 配置继承此类的子类
      // sourceBuilder.query(QueryBuilders.matchAllQuery()) // 设置 SearchSourceBuilder 为 match_all 查询
      // sourceBuilder.query(QueryBuilders.wildcardQuery("name", "query"))
  	// sourceBuilder.query(QueryBuilders.matchQuery("key", "value"));
      .from(0) // 页
      .size(2) // 页显示的文档数
      .sort("name.keyword", SortOrder.ASC) // 排序 ASC: 小 -> 大 DESC: 大 -> 小
      // 或 .sort(new ScoreSortBuilder().order(SortOrder.ASC)); // 根据权值排序
      // 或 .sort(new FieldSortBuilder("id").order(SortOrder.ASC)); // 类使用字符串的
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
      .timeout(TimeValue.timeValueSeconds(3)); // 超时时间
  
  sourceBuilder.profile(true); // 启用 Profile API (Profile API 可用于对特定搜索请求的查询和聚合的执行进行解析)
  
  SearchRequest request = new SearchRequest("test2"); // 创建 SearchRequest 
  											    // 可设置一个或多个索引 也可后面再配置索引
  // 配置请求 SearchRequest
  request.source(sourceBuilder); // 将 SearchSourceBuilder 加入 SearchRequest
  // request.indices(String 索引, ...); // 配置索引
  // request.routing("routing"); // 配置路由
  // request.indicesOptions(IndicesOptions.lenientExpandOpen()); // 处理不可用的索引 & 拓展通配符表达式
  // request.preference("_local"); // 设置分片偏好, 例为 在本地分片查询 (默认 随机访问分片)
  ```

+ Search Response

  > 阻塞式 & 异步

  ```java
  // 异步
  ActionListener<SearchResponse> listener = new ActionListener<SearchResponse>() {
      @Override
      public void onResponse(SearchResponse searchResponse) {
          // 响应成功处理
      }
      @Override
      public void onFailure(Exception e) {
          // 响应失败处理
      }
  };
  client.searchAsync(request, RequestOptions.DEFAULT, listener);
  
  // 或
  client.searchAsync(request, RequestOptions.DEFAULT, new ActionListener<SearchResponse>() {
      @Override
      public void onResponse(SearchResponse searchResponse) {
          // 响应成功处理
      }
      @Override
      public void onFailure(Exception e) {
          // 响应失败处理
      }
  });
  ```

  ```java
  // 阻塞式
  client.search(request, RequestOptions.DEFAULT);
  ```

+ 处理响应

  ```java
  SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
  RestStatus status = searchResponse.status();
  TimeValue took = searchResponse.getTook(); // 执行耗时
  Boolean terminatedEarly = searchResponse.isTerminatedEarly(); // 是否提前终止
  boolean timedOut = searchResponse.isTimedOut();
  int totalShards = searchResponse.getTotalShards();
  int successfulShards = searchResponse.getSuccessfulShards();
  int failedShards = searchResponse.getFailedShards(); // 返回 失败查询 数量
  for (ShardSearchFailure failure : searchResponse.getShardFailures() /*返回失败的查询请求*/ ) { 
      // 失败的查询处理
  }
  // 获取查询的文档数据及其相应信息 (有两层, 第一层仅为 SearchHits 为索引信息, 第二层为 SearchHits[] 为查询到的文档信息)
  SearchHits hits = searchResponse.getHits();
  // TotalHits 需要使用 Relation 处理获得信息
  TotalHits totalHits = hits.getTotalHits();
  // 查询到的文档数量
  long numHits = totalHits.value;
  // 获取最高值(EQUAL_TO) 或 最低值(GREATER_THAN_OR_EQUAL_TO)
  TotalHits.Relation relation = totalHits.relation;
  float maxScore = hits.getMaxScore();
  // 获得第二层 Hits
  SearchHit[] searchHits = hits.getHits();
  for (SearchHit hit : searchHits) {
      // 对每一个查询到的文档进行处理
      String index = hit.getIndex();
      String id = hit.getId();
      float score = hit.getScore();
      // 获取文档数据 _source
      String sourceAsString = hit.getSourceAsString();
      Map<String, Object> sourceAsMap = hit.getSourceAsMap();
      String documentTitle = (String) sourceAsMap.get("title");
      List<Object> users = (List<Object>) sourceAsMap.get("user");
      Map<String, Object> innerObject =
          (Map<String, Object>) sourceAsMap.get("innerObject");
      // 获取设置了 Highlight 的文档的 highlight 部分
      Map<String, HighlightField> highlightFields = hit.getHighlightFields();
      HighlightField highlight = highlightFields.get("title"); 
      Text[] fragments = highlight.fragments();  
      String fragmentString = fragments[0].string();
      
  }
  // 从 SearchResponse 中获取聚合结果
  Aggregations aggregations = searchResponse.getAggregations(); 
  // Range range = aggregations.get("by_company"); // 使用 range 聚合
  Terms byCompanyAggregation = aggregations.get("by_company"); // terms 聚合 对应的类需要匹配(根据自己请求中聚合的配置)
  Bucket elasticBucket = byCompanyAggregation.getBucketByKey("Elastic"); // 获得 key 标记为 Elastic 的 桶 
  Avg averageAge = elasticBucket.getAggregations().get("average_age"); // 获得 上述桶 的子聚合 avg 的结果
  double avg = averageAge.getValue();
  // 可使用 Map 获取 所有聚合 Map<String, Aggregation> String 为聚合名
  // 可使用 List 获取 所有聚合 List<Aggregation>
  List<Aggregation> aggregationList = aggregations.asList();
  for (Aggregation agg : aggregations) {
      String type = agg.getType();
      if (type.equals(TermsAggregationBuilder.NAME)) {
          Bucket elasticBucket = ((Terms) agg).getBucketByKey("Elastic");
          long numberOfDocs = elasticBucket.getDocCount();
      }
  }
  ```

  

