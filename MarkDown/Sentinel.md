# Sentinel
> 翻译: 哨兵
- 作用: 对现有微服务系统进行保护 ==> 替换 Hystrix
    ![Sentinel 作用](https://img-blog.csdnimg.cn/20191130203727708.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L25vYW1hbl93Z3M=,size_16,color_FFFFFF,t_70)
    > 解决访问雪崩 ==> 服务熔断机制(服务降级) <br>
    > 服务流控 <br>
    > 服务负载保护
- 历史:
    > 2012 诞生
    > 2013-2017 发展,积累经验
    > 2018 开源
    > 2019 C++ 原生版本
    > 2020 Sentinel GO 版本 (云原生)
+ 概念:
    - 资源 resource:
        > 可以是 Java 中的任何内容 <br>
        > 狭义: 微服务的访问 rest 接口 <br>
    - 规则 rule:
        > 流控规则 熔断规则 负载规则...
+ 特性:
    - 丰富的应用场景
    - 完善的实时监控
    - 广泛的开源生态
+ 使用:
    > + Sentinel
    >   - sentiel: 项目中实现 流量控制、熔断、降级
    >   - sentiel dashboard: 监控/控制 ==> 配置资源与规则的对应关系
    1. Sentinel DashBoard
        > 配置/控制微服务
        - 下载 jar 包
        - 运行
            > 默认端口: 8080 <br>
            > 指定端口运行: java -jar -Deserver.port=port xxx.jar
        - 访问 dashboard 管理界面
            > username: sentiel <br>
            > password: sentiel <br>
            > 未配置资源,dashboard 内为空. <br>
            > 规则是基于资源的 <br>
            > 由于 Sentinel Dashboard 是 SpringBoot 项目,因此具有 Session 时间.默认 30min. 可通过*修改配置*或*添加命令行参数* `-Dserver.servlet.session.timeout=` (单位: s) 
    2. 创建业务服务
        > 注册到服务注册中心
    3. 引依赖
        > `spring-cloud-starter-alibaba-sentinel`
    4. 写配置
        ```yaml
        spring:
            cloud:
                sentiel:
                    enable: true # 开启 sentiel 默认: 开启
                    transport:
                        dashboard: host:port # 连接 dashboard
                        port: port # 与 dashboard 通信的端口 默认: 8719 一般不改,一改两边都要改
        ``` 
        
        > 需要有调用, dashboard 上的信息才会初始化 <br>
        > 系统吞吐量相关: <br>
        > &nbsp;&nbsp;&nbsp;&nbsp;QPS(Query-Per-Second): 系统每秒请求量
        > &nbsp;&nbsp;&nbsp;&nbsp;RT(Response Time): 响应时间 (单位:ms)
+ 规则配置:
    > sentinel 提供五种规则: 流控规则 熔断规则 热点规则 系统规则 授权规则 <br>
    > 每次服务重启,规则需要重新配置
    + [流控规则](https://sentinelguard.io/zh-cn/docs/flow-control.html): 
        - 流量控制(flow control): 监控应用流量的 ***QPS*** 或***并发线程数***指标,当达到某个指标时进行控制,避免被过高的瞬时流量冲垮,从而保障应用的高可用.
        ![流量控制](../image/Sentinel(FlowControl).png)
        - QPS: 每秒请求数 ==> 达到每秒请求数的阈值后对当前请求进行限流.
        - 并发线程数 ==> 一个请求对应一个线程,但并发线程数与请求数无关.仅当服务器内部创建的线程数超过指定阈值后,对当前请求进行限流.
            > Jmeter: 压力测试工具(纯 Java 开发) 并行的发送请求 <br>
            > &nbsp;&nbsp;&nbsp;&nbsp;使用: bin 中 jmeter.bat 打开 GUI 页面(Option 内有语言选择)
            > 1. 为测试计划命名
            > 2. 创建线程组 (Ramp-Up 时间: 两个线程相隔多少时间发送请求;调度器: 延迟发送)
            > 3. 添加 Http 请求(取样器中)
            > 4. (选) 添加查看结果树 (监听器中)
            > <br> [JMeter 配置与使用](https://cloud.tencent.com/developer/article/1633626)
        + 高级选项:
            + 流控模式:
                1. 直接
                    > 配置资源在运行过程中超过阈值之后对后续资源请求做什么处理 <br>
                    > &nbsp;&nbsp;&nbsp;&nbsp;正向关联
                2. 关联
                    > 配置资源在运行过程中超过阈值之后对关联资源做什么处理 <br>
                    > &nbsp;&nbsp;&nbsp;&nbsp;反向关联: 指定关联资源对该资源的访问超过阈值时,指定关联资源对该资源的访问根据流控效果产生相应结果 
                3. 链路(有 BUG)
                    > 配置资源在运行过程中超过阈值之后对链路资源做什么处理 <br>
                    > &nbsp;&nbsp;&nbsp;&nbsp;正向关联: 当该资源请求访问达到阈值时,对指定入口资源的访问根据流控效果产生相应结果
            + 流控效果:
                > 只适用于 QPS 限流
                1. 快速失败
                    > 直接拒绝请求,并抛出相应异常(流控异常)
                2. Warm Up
                    > 冷启动/预热 缓慢增长请求处理量(在预热时间之内慢慢增长)
                3. 排队等候  
                    > 始终匀速放行请求 超过阈值的请求等待正在处理的请求完成再处理
    + [熔断规则](https://sentinelguard.io/zh-cn/docs/circuit-breaking.html):
        - 熔断降级(Degrade Service): 监控应用中的资源调用情况,达到阈值时自动触发熔断降级(避免服务雪崩)
            > 原理: 监控调用链路的每一个服务,当某个服务在一段时间内出现异常的概率达到阈值,自动触发熔断(服务在熔断状态下不可用)
        + 降级策略:
            1. RT (响应时间) / 慢调用比例 
                > 单位: ms <br>
                > 时间窗口: 熔断状态保持时间,过了时间直接退出熔断状态 <br>
                > 在 1s 内大于等于 5 个请求的RT均大于阈值,则进入熔断状态 <br> <hr>
                > ***新版本: 慢调用比例*** <br>
                > 最大 RT: ms <br>
                > 比例阈值: 0.0 ~ 1.0 <br>
                > 熔断时长: s <br>
                > 最小请求数: 默认 5 <br>
                > 统计时长: ms 默认 1000ms <br>
                >> **单位统计时长**内请求数目大于设置的最小请求数目,并且慢调用的比例大于阈值(慢调用比例: 大于最小请求数的请求,出现 RT 大于最大 RT 的请求占所有请求的比例),则接下来的熔断时长内请求会自动被熔断.<br>
                >> 经过熔断时长后熔断器会进入探测恢复状态(半开),若接下来的一个请求响应时间小于设置的慢调用 RT 则结束熔断,若大于设置的慢调用 RT 则会再次被熔断.
            2. 异常比例/异常百分比
                > 异常比例: 0.0 ~ 1.0 <br>
                > 熔断时长: s <br>
                > 最小请求数: 默认 5 <br>
                > 统计时长: ms 默认 1000ms <br>
                >> 在**单位统计时长**内,请求数大于最小请求数,且异常的比例大于阈值,则接下来的熔断时长内请求会自动被熔断. <br>
                >> 经过熔断时长后熔断器会进入探测恢复状态,若接下来的一个请求成功完成(没有错误)则结束熔断,否则会再次被熔断.
            3. 异常数
                > 异常数: <br>
                > 熔断时长: s <br>
                > 最小请求数: 默认 5 <br>
                > 统计时长: ms 默认 1000ms <br>
                >> 当**单位统计时长**内的异常数目超过阈值之后会自动进行熔断. <br>
                >> 经过熔断时长后熔断器会进入探测恢复状态,若接下来的一个请求成功完成(没有错误)则结束熔断,否则会再次被熔断.
    + [热点规则](https://sentinelguard.io/zh-cn/docs/parameter-flow-control.html):
        - 热点[经常访问的数据]参数(ParamFlow)限流: 统计热点数据中访问次数最高的 Top K 数据,并对其进行访问限制.***即***, 访问资源中携带指定参数则执行限流. 
            > Top K/Top N 数据: 前 N 
        + 规则内项目:
            1. 资源名: <del>资源路径</del> 资源别名
                > 使用资源路径无效 <br>
                > 通过添加 `@SentinelResource(value = "")` 进行资源别名的设定
            2. 限流模式: QPS 模式(1.8.3 只有限流)
            3. 参数索引: 0 开始
            4. 单机阈值: 带有指定参数的访问数超过该项设定的值则抛出异常(`@SentinelResource`没有设定处理方法)
            5. 统计窗口时长
            6. 高级选项:
                1. 参数类型: 
                    - int 
                    - double 
                    - long 
                    - float 
                    - byte 
                    - char 
                    - java.lang.String
                2. 参数值
                3. 限流阈值
                    > 指定参数索引的值为何值时,超过阈值进行限流. <br>
                    > 可设置多个 <br>
        - `@SentinelResource`
            > ***注***: 参数类型一定要一致 (fallback/defaultFallback 和 blockHandler)
            1. value : 别名
            2. blockHandler : 热点参数限流后的处理方式 (自定义)
            > 例子
            ```java
            public String blockHandler(Integer id, BlockException ex) {
                if (ex instanceof FlowException) {
                    return "流控";
                } else if (ex instanceof ParamFlowException) {
                    return "热点参数";
                } else if (ex instanceof DegradeException) {
                    return "降级";
                } else if (ex instanceof AuthorityException) {
                    return "权限";
                } eles if (ex instanceof SystemBlockException) {
                    return "系统";
                } else {
                    return "Unknown Exception"; 
                }
            }
            ```
            > BlockException : 子类对应每一个限流异常 <br>
            > 即 可以根据不同的限流异常编写不同的处理方式 <br>
            3. fallback : 自定义业务异常的默认处理方式
            ```java
            public String fall(Integer id) {
                return "Error";
            }
            ```
            4. defaultFallback : 业务异常的默认处理方式
    + [系统规则](https://sentinelguard.io/zh-cn/docs/system-adaptive-protection.html):
        - 系统自适应限流(SystemFlow): 从整体维度对应用入口流量进行控制 
    + [授权规则](https://sentinelguard.io/zh-cn/docs/origin-authority-control.html):
        - 黑白名单控制/来源访问控制: 根据调用来源 判断/限制 请求是否放行
            > 配置了黑名单,黑名单内的才能不放行,其余放行.
- 簇点链路:
    > 显示各个资源的状态,并且可以在此选项卡内添加流控、熔断、热点、授权规则.
+ 动态配置规则
    - 通过 API 直接修改
        ```java
        FlowRuleManager.loadRules(List<FlowRule> rules); // 修改流控规则
        DegradeRuleManager.loadRules(List<DegradeRule> rules); // 修改降级规则
        ```
        > 只接受内存态的规则对象,不具备数据持久化的要求(规则在资源退出后也跟着一起清除). <br>
        > 注意: 要使客户端具备规则 API,需在客户端引入以下依赖:
        ```xml
        <dependency>
            <groupId>com.alibaba.csp</groupId>
            <artifactId>sentienl-http-simple-transport</artifactId>
            <version>x.y.z</version>
        </dependency>
        ```
    + 通过 DataSource 适配不同的数据源修改
        + 实现方式:
            + 拉模式
                > client / 微服务 主动给向规则管理中心定期轮巡拉取规则. <br>
                > 规则管理中心: RDBMS 文件 VCS ... <br>
                > 缺: 无法及时获取配置变更
                - 扩展: 实现拉模式的数据源最简单的方式是继承 ***AutoRefreshDataSource*** 抽象类,然后实现 ***readSource()*** 方法,在该方法里从指定数据源读取**字符串格式**的配置数据。
                > 客户端引入依赖:
                ```xml
                <dependency>
                    <groupId>com.alibaba.csp</groupId>
                    <artifactId>sentinel-datasource-extension</artifactId>
                    <version>x.y.z</version>
                </dependency>
                ```
            + 推模式
                > 规则中心统一推送, client / 微服务 通过注册监听器的方式时刻监听变化 <br>
                > 配置中心: Nacos ZooKeeper ... <br>
                > 优: 良好的实时性 和 一致性
                - 扩展: 实现推模式的数据源最简单的方式是继承 ***AbstractDataSource*** 抽象类,在其**构造方法中添加监听器**,并实现 ***readSource()*** 从指定数据源读取**字符串格式**的配置数据。
                > 引入对应数据源的依赖(此以 Nacos 为例):
                ```xml
                <dependency>
                    <groupId>com.alibaba.csp</groupId>
                    <artifactId>sentinel-datasource-nacos</artifactId>
                    <version>x.y.z</version>
                </dependency>
                ```
                > <a href="#Nacos DataSources">后见下</a> (配置文件配置) <br> 编写注册类的方式见注册数据源

        + 注册数据源:
            - 手动:
                ```java
                ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId, parser);
                FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
                ```
                > 将数据源注册至指定的规则管理器
            - 自动: 
                > 借助 Sentinel 的 InitFunc SPI 扩展接口(即 名为 InitFunc 的 interface). <br>
                > 编写 实现 InitFunc 接口的 实现类,并且在 init 方法内编写注册数据源的逻辑. <br>
                ```java
                public class DataSourceInitFunc implements InitFunc {
                    @Override
                    public void init() throws Exception {
                        final String remoteAddress = "localhost";
                        final String groupId = "Sentinel:Demo";
                        final String dataId = "com.alibaba.csp.sentinel.demo.flow.rule";
                        
                        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId,
                            source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
                        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
                    }
                }
                ```
            - 后续: 接着将对应的类名添加到位于资源目录(通常是 resource 目录)下的 META-INF/services 目录下的 com.alibaba.csp.sentinel.init.InitFunc 文件中,比如: com.test.init.DataSourceInitFunc 这样,当初次访问任意资源的时候,Sentinel 就可以自动去注册对应的数据源了.
+ <a id="Nacos DataSources">使用 Nacos 持久化 Sentinel 规则</a>
    > 默认 Sentinel 的规则在资源退出后也跟着一起清除,为了重启后仍可使用之前设定好的规则,因此需要将其持久化
    + Sentinel 使用 Nacos 存储规则
        + 持久化规则配置方法:
            - 文件配置
            - Nacos/ZooKeeper/Apollo 配置
        + 使用 Nacos 存储规则配置
            1. 引依赖
                `sentinel-datasource-nacos` 
                > 自然, Sentinel 和 Nacos 客户端的依赖也需要配置
            2. 写配置
                ```yml
                spring:
                    cloud:
                        sentinel:
                            datasource:
                                ds1:
                                    nacos:
                                        server-addr: localhost:8848 # Nacos 地址;可写使用 Nginx 负载均衡后的地址
                                        dataId: ${spring.application.name}-sentinel # 推荐
                                        groupId: DEFAULT_GROUP
                                        rule-type: flow
                                ds2:
                                    nacos:
                                        server-addr: localhost:8848
                                        data-id: ${spring.application.name}-sentinel-degrade
                                        group-id: DEFAULT_GROUP
                                        rule-type: degrade
                ```
                > rule-type: 用来定义存储的规则类型. <br> 
                > 所有的规则类型可查看枚举类(`sentinel-datasource-nacos`包内的)：<br> 
                > `RuleType` <br> 
                > 每种规则的定义格式可以通过各枚举值中定义的规则对象来查看 <br> 
                > 比如限流规则可查看(`sentinel-datasource-nacos`包内的)：`FlowRule` <br>
                > 可以通过查看 sentinel-datasource-nacos 下的 `DataSourcePropertiesConfiguration` 类 和 `NacosDataSourceProperties` 类分析配置内容. <br>
                > 例如: NacosDataSourceProperties
                ```Java
                public class NacosDataSourceProperties extends AbstractDataSourceProperties {
                    private String serverAddr;
                    private String contextPath;
                    private String username;
                    private String password;
                    @NotEmpty
                    private String groupId = "DEFAULT_GROUP";
                    @NotEmpty
                    private String dataId;
                    private String endpoint;
                    private String namespace;
                    private String accessKey;
                    private String secretKey;
                    ...
                }
                ```
            3. 创建启动类和 Rest 接口
            4. 在 Nacos 中创建限流规则配置 (JSON 格式数组)
                - 每个规则类型不同,其格式也不同.所以一个配置文件,只能对应一种规则类型,当你想配置所有的规则时,需要创建不同的配置文件.
                - 通过查看 `sentinel-datasource-nacos` 下的 AbstractRule 抽象类的子类,分析 JSON 文件的属性
                - 流控
                    ```JSON
                    [
                        {
                            "resource": "/nacos-client",
                            "limitApp": "default",
                            "grade": 1,
                            "count": 5,
                            "strategy": 0,
                            "controlBehavior": 0,
                            "clusterMode": false
                        }
                    ]
                    ```
                    > 数组类型;数组中的每个对象是针对每一个保护资源的配置对象<br>
                    > 属性: <br>
                    > resource: 资源名(限流规则作用对象) <br>
                    > limitApp: 流控针对的调用来源,若为 default 则不区分调用来源 <br>
                    > grade: 限流阈值类型;0 并发线程数, 1 QPS <br>
                    > count: 限流阈值 <br>
                    > strategy: 调用关系限流策略/流控模式(直接、关联、链路) <br>
                    > controlBehavior: 流控效果 (直接拒绝、Warm up、排队) <br>
                    > clusterMode: 是否为集群模式 <br>
                    > 注: 当 grade 设为 0 时, controlBehavior 设置无效
                - 降级
                    ```json
                    [
                        {
                            "count": 500,
                            "grade": 0,
                            "limitApp": "default",
                            "minRequestAmount": 5,
                            "resource": "/test",
                            "slowRatioThreshold": 1,
                            "statIntervalMs": 1000,
                            "timeWindow": 100
                        }
                    ]
                    ```
                    > count: 阈值(慢调用的最大 RT; 异常比例的 比例阈值; 异常数的 异常数) <br>
                    > grade: 熔断策略(慢调用 异常比例 异常数) <br>
                    > limitApp: 流控针对的调用来源(Sentinel 控制台不显示) <br>
                    > minRequestAmount: 最小请求数 <br>
                    > resource: 资源名 <br>
                    > slowRatioThreshold: 慢调用/RT 的比例阈值 <br>
                    > statIntervalMs: 统计时长 <br>
                    > timeWindow: 熔断时长 <br>

            5. 启动 调用一次接口,查看 Sentinel 控制台.此时,在 Nacos 中设置的 限流配置可以在 Sentinel 中看到
    + Sentinel Dashboard 中修改的配置同步到 Nacos
        + 同步规则到 Nacos 配置文件 
            - 修改 Sentinel Dashboard 源代码 (Sentinel-x.x.x 项目的 sentinel-dashboard module)
                > 添加 Rule 对于 Nacos 的配置信息 (NacosConfig RuleProvider&RulePublisher) <br>
                > 3 4 5 6 相应配置在同 Module 的 test 中已存在,可以直接拉取并加一些自定义配置
            1. 修改 pom.xml 中的 sentinel-datasource-nacos 依赖
                > <scope>test</scope> 删去/注释掉
            2. 修改 webapp/resources/app/scripts/directives/sidebar/sidebar.html 中的 流控规则 一块
                ```xml
                <li ui-sref-active="active">
                    <a ui-sref="dashboard.flowV1({app: entry.app})">
                        <i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;流控规则
                    </a>
                </li>
                ```
                修改为
                ```xml
                <li ui-sref-active="active">
                    <a ui-sref="dashboard.flow({app: entry.app})">
                        <i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;流控规则
                    </a>
                </li>
                ```
                > flowV1 --> flow
            3. 创建 nacos 包
                > com.alibaba.csp.sentinel.dashboard.rule 下新建 nacos 包,编写针对 Nacos 的拓展实现
            4. 创建 Nacos 的配置类
                ```java
                @Configuration
                public class NacosConfig {

                    @Bean
                    public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
                        return JSON::toJSONString;
                    }

                    @Bean
                    public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
                        return s -> JSON.parseArray(s, FlowRuleEntity.class);
                    }

                    @Bean
                    public ConfigService nacosConfigService() throws Exception {
                        Properties properties = new Properties();
                        properties.put(PropertyKeyConst.SERVER_ADDR, "localhost");
                        // namespace 设定
                        // properties.put(PropertyKeyConst.NAMESPACE, "130e71fa-97fe-467d-ad77-967456f2c16d");
                        return ConfigFactory.createConfigService(properties);
                    }
                }
                ```
            5. 实现 Nacos 配置拉取
                ```java
                @Component("flowRuleNacosProvider")
                public class FlowRuleNacosProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {

                    @Autowired
                    private ConfigService configService;
                    @Autowired
                    private Converter<String, List<FlowRuleEntity>> converter;

                    public static final String FLOW_DATA_ID_POSTFIX = "-sentinel"; // 后缀
                    public static final String GROUP_ID = "DEFAULT_GROUP";

                    @Override
                    public List<FlowRuleEntity> getRules(String appName) throws Exception {
                        String rules = configService.getConfig(appName + FLOW_DATA_ID_POSTFIX, GROUP_ID, 3000);
                        if (StringUtil.isEmpty(rules)) {
                            return new ArrayList<>();
                        }
                        return converter.convert(rules);
                    }
                }
                ```
                > getRules 方法中的 appName 参数是 Sentinel 中的**服务名称/dataId**. <br> <hr>
                > configService.getConfig 方法是从 Nacos 中获取配置信息的具体操作.其中,DataId 和 GroupId 分别对应客户端使用时候的对应配置.<br>
                >> 两边的 dataId 和 groupId 必须对应
            6. 实现 Nacos 配置推送
                ```java
                @Component("flowRuleNacosPublisher")
                public class FlowRuleNacosPublisher implements DynamicRulePublisher<List<FlowRuleEntity>> {

                    @Autowired
                    private ConfigService configService;
                    @Autowired
                    private Converter<List<FlowRuleEntity>, String> converter;

                    public static final String FLOW_DATA_ID_POSTFIX = "-sentinel";
                    public static final String GROUP_ID = "DEFAULT_GROUP";

                    @Override
                    public void publish(String app, List<FlowRuleEntity> rules) throws Exception {
                        AssertUtil.notEmpty(app, "app name cannot be empty");
                        if (rules == null) {
                            return;
                        }
                        configService.publishConfig(app + FLOW_DATA_ID_POSTFIX, GROUP_ID, converter.convert(rules));
                    }
                }
                ```
            7. 修改 `com.alibaba.csp.sentinel.dashboard.controller.v2.FlowControllerV2` 中的 `DynamicRuleProvider` 和 `DynamicRulePublisher` 注入的 Bean
                ```java
                @Autowired
                @Qualifier("flowRuleNacosProvider")
                private DynamicRuleProvider<List<FlowRuleEntity>> ruleProvider;
                @Autowired
                @Qualifier("flowRuleNacosPublisher")
                private DynamicRulePublisher<List<FlowRuleEntity>> rulePublisher;
                ```
            + 总结:
                1. 修改依赖使 NacosDataSource 可用
                2. 在 HTML 页面上显示同步到 Nacos 的选项卡
                3. 创建 Nacos 的配置包并创建相应配置
                4. 修改 controller 中对应 Rule 接口中注入的 Bean
            + 产生的问题:
                - Nacos 修改规则后, Sentinel 规则列表配置不会自动刷新.
+ `@SentinelResource` 注解:
    > 若您是通过 Spring Cloud Alibaba 接入的 Sentinel,则无需额外进行配置即可使用 `@SentinelResource` 注解. 即引入依赖 spring-cloud-starter-alibaba-sentinel <br>
    > 但不是引入上述依赖,则需要将 SentinelResourceAspect 注册成 Spring Bean, 才能使用 @SentinelResource 注解
    + 注入 SentinelResourceAspect 的 Bean 到 SpringBoot Ioc 容器中
        > SentinelResourceAspect (com/alibaba/csp/sentinel/annotation/aspectj/SentinelResourceAspect.java): <br>
        > invokeResourceWithSentinel 方法通过 Aspect 的 around 拦截标注了 @SentinelResource 的方法; <br>
        > 在进入该方法创建了一系列处理对象后,准备处理方法前,调用 SphU.entry(resourceName, resourceType, entryType, args) 获取 Entry 对象,再开始处理调用链路; <br>
        > 在处理链路后(无论是否产生异常),退出方法之前,调用 entry 对象的 exit() 方法,再退出. <br>
        > 链路 BlockException 时,调用 handleBlockException() 方法处理 <br>

        > 1.8.3 版本的 SentinelResourceAspect
        ```java
        @Aspect
        public class SentinelResourceAspect extends AbstractSentinelAspectSupport {
            public SentinelResourceAspect() {
            }

            @Pointcut("@annotation(com.alibaba.csp.sentinel.annotation.SentinelResource)")
            public void sentinelResourceAnnotationPointcut() {
            }

            @Around("sentinelResourceAnnotationPointcut()")
            public Object invokeResourceWithSentinel(ProceedingJoinPoint pjp) throws Throwable {
                Method originMethod = this.resolveMethod(pjp);
                SentinelResource annotation = (SentinelResource)originMethod.getAnnotation(SentinelResource.class);
                if (annotation == null) {
                    throw new IllegalStateException("Wrong state for SentinelResource annotation");
                } else {
                    String resourceName = this.getResourceName(annotation.value(), originMethod);
                    EntryType entryType = annotation.entryType();
                    int resourceType = annotation.resourceType();
                    Entry entry = null;

                    Object var10;
                    try {
                        try {
                            entry = SphU.entry(resourceName, resourceType, entryType, pjp.getArgs());
                            Object var8 = pjp.proceed();
                            return var8;
                        } catch (BlockException var15) {
                            Object var18 = this.handleBlockException(pjp, annotation, var15);
                            return var18;
                        } catch (Throwable var16) {
                            Class<? extends Throwable>[] exceptionsToIgnore = annotation.exceptionsToIgnore();
                            if (exceptionsToIgnore.length > 0 && this.exceptionBelongsTo(var16, exceptionsToIgnore)) {
                                throw var16;
                            }
                        }

                        if (!this.exceptionBelongsTo(var16, annotation.exceptionsToTrace())) {
                            throw var16;
                        }

                        this.traceException(var16);
                        var10 = this.handleFallback(pjp, annotation, var16);
                    } finally {
                        if (entry != null) {
                            entry.exit(1, pjp.getArgs());
                        }

                    }

                    return var10;
                }
            }
        }
        ```
        - handleBlockException() 方法:
            ```java
            protected Object handleBlockException(ProceedingJoinPoint pjp, SentinelResource annotation, BlockException ex) throws Throwable {
                Method blockHandlerMethod = this.extractBlockHandlerMethod(pjp, annotation.blockHandler(), annotation.blockHandlerClass());
                if (blockHandlerMethod != null) {
                    Object[] originArgs = pjp.getArgs();
                    Object[] args = Arrays.copyOf(originArgs, originArgs.length + 1);
                    args[args.length - 1] = ex;
                    return this.invoke(pjp, blockHandlerMethod, args);
                } else {
                    return this.handleFallback(pjp, annotation, ex);
                }
            }
            ```
            > ***产生了异常才会调用该方法***<br>
            > 首先判断是否定义了 blockHandler 方法 或 产生的是否为 BlockException: <br>
            > &nbsp;&nbsp;&nbsp;&nbsp;没有定义或不是 BlockException 则调用 handleFellback (直接判断为业务异常) <br>
            > &nbsp;&nbsp;&nbsp;&nbsp;定义了或是 BlockException 则将参数列表直接放入原函数参数列表长度的数组(最后放异常),然后调用方法. <br>
            > 即产生了 BlockException 且有 blockhandler 才走 blockhandler 这条线路
        - handleFallback() 方法:        
            ```java
            protected Object handleFallback(ProceedingJoinPoint pjp, SentinelResource annotation, Throwable ex) throws Throwable {
                return this.handleFallback(pjp, annotation.fallback(), annotation.defaultFallback(), annotation.fallbackClass(), ex);
            }

            protected Object handleFallback(ProceedingJoinPoint pjp, String fallback, String defaultFallback, Class<?>[] fallbackClass, Throwable ex) throws Throwable {
                Object[] originArgs = pjp.getArgs();
                Method fallbackMethod = this.extractFallbackMethod(pjp, fallback, fallbackClass);
                if (fallbackMethod != null) {
                    int paramCount = fallbackMethod.getParameterTypes().length;
                    Object[] args;
                    if (paramCount == originArgs.length) {
                        args = originArgs;
                    } else {
                        args = Arrays.copyOf(originArgs, originArgs.length + 1);
                        args[args.length - 1] = ex;
                    }

                    return this.invoke(pjp, fallbackMethod, args);
                } else {
                    return this.handleDefaultFallback(pjp, defaultFallback, fallbackClass, ex);
                }
            }
            ```
            > 首先 handleFallback 方法判断 @SentinelResource 标注的方法是否定义了 fanllback 方法. <br>
            > &nbsp;&nbsp;&nbsp;&nbsp;没有 (=null) 则调用 defaultFallback 方法 (没有定义 defaultFallback 则直接抛出错误/异常)
            > &nbsp;&nbsp;&nbsp;&nbsp;有 (!=null) 则判断自定义的 fallback 参数列表和原函数的参数列表的长度一致.<br>
            > &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;长度相同则直接套用自定义的参数列表. <br>
            > &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;长度不同则创建和原函数参数列表相同长度的数组并将 fallback 函数参数列表放入,再添加接异常在数组的末尾.
    + `@SentinelResource` 注解包含属性:
        - value: 资源名(必填)
        - entryType: entry 类型 (默认: EntryType.OUT)
        - blockHandler/blockHandlerClass: 处理 BlockException 的函数名/处理 BlockException 的其他类(的函数;即处理函数定义在其他类中,且这个函数必须为 static 函数)的类对象.
            > blockHandler 函数要求: <br>
            > &nbsp;&nbsp;函数访问范围: public <br>
            > &nbsp;&nbsp;返回类型: 与原方法对应 <br>
            > &nbsp;&nbsp;参数列表: 和原方法匹配并最后加一个额外的 BlockException <br>
            > <hr>
            > blockHandlerClass 的使用需要配合 blockHandler 指定对应 static 函数的名称
        - fallback/fallbackClass: 处理业务逻辑异常的 fallback 处理逻辑 (fallback 同类中的函数|fallbackClass 其他类的类对象(类中有包含对应处理异常的 static 函数))
            > 针对所有(除了 exceptionToIgnore 的)异常.函数要求: <br>
            > &nbsp;&nbsp;函数访问范围: public <br>
            > &nbsp;&nbsp;返回值类型: 与原方法一致 <br>
            > &nbsp;&nbsp;参数列表: 原函数一致,或者可以额外多一个 Throwable 类型的参数用于接收对应的异常. <br>
            > <hr>
            > fallbackClass 的使用需要配合 fallback 指定对应 static 函数的名称
        - defaultFallback: 默认 fallback 函数名称(通用 fallback) ***优先级低于 fallback 函数***(同时配置 fallback 和 defaultFallback, 则只有 fallback 生效)
            > 细节与 fallback 一致 <br>
            > 1.6.0 之前的版本 fallback 函数只针对降级异常(DegradeException)进行处理,不能针对业务异常进行处理.
            > 1.8.0 版本开始,defaultFallback 支持在类级别进行配置. <br>
        - exceptionToIgnore: 指定被排除的异常(不被统计,不被 fallback 处理,保持原样输出)
    + 细节:
        - 若 blockHandler 和 fallback 都进行了配置,则***被限流降级而抛出 BlockException 时只会进入 blockHandler 处理逻辑***.
        - 若未配置 blockHandler、fallback 和 defaultFallback,则被限流降级时会将 BlockException 直接抛出
            > 若方法本身未定义 throws BlockException,则会被 JVM 包装一层 UndeclaredThrowableException.
        - 从 1.4.0 版本开始,注解方式定义资源支持自动统计业务异常,无需手动调用 Tracer.trace(ex) 来记录业务异常.
            > 1.4.0 之前需要自行配置 Tracer.trace(ex) 记录异常
        - `@SentinelResource` 注解应该设置在 Service 层上
            > Controller 层的路径已经被记录,可以在 Dashboard 上设定规则,没有必要再使用 `@SentinelResource` 注解标记
