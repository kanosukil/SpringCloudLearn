# MQ
> Message Queue: 消息队列
+ 系统间数据传递;保证消息传递顺序,实现系统间数据传递
+ 应用场景/作用:
    - 异构系统间***解耦***
        > 系统发送消息到 MQ, 需要该消息的系统再向 MQ 请求 <br>
        > 避免了系统 A 向系统 B 发消息,系统 B 不再需要或系统 C 需要而在系统 A 中增删代码的问题.
    - ***异步***处理 优化程序性能
        > 服务 A 的请求需要服务 B C D 完成 <br>
        > 原流程是: 服务 B C D 全完成后才响应服务 A <br>
        > 使用 MQ 后: 服务 A 的请求发送到 MQ 后即可得到响应, 服务B C D 在后台完成服务 A 的请求.
    - ***削峰*** 减小高峰期压力
        > MQ 可以接受短时间积压数据 <br>
        > 高峰期请求首先由 MQ 接收, 消费者可以先拉取一部分可以接受的请求执行,以减少压力避免压力过大而导致宕机
+ RabbitMQ
    - Erlang 开发;实现 AMQP (高级消息队列协议);开源消息中间件
    + 特点: 
        - 可靠性
            > 支持持久化 传递确认 发布确认...
        - 灵活的分布消息策略 ☆
            > 消息进入 MQ 前由 Exchange(交换机) 进行路由消息. <br>
            > 分发策略: 简单模式 工作队列模式 发布订阅模式 路由模式 通配符模式
        - 支持集群
            > 多台 RabbitMQ 服务器可组成一个集群
        - 多种协议
            > STOMP MQTT...
        - 支持多种语言客户端
            > 几乎所有常用编程语言: Java .NET Ruby...
        - 可视化管理界面
            > 监控和管理消息 Broker
        - 插件机制
    + 安装:
        + 安装 Erlang 语言 (RabbitMQ 基于该语言开发) [下载](https://www.erlang.org/downloads?spm=a2c6h.12873639.0.0.433733dfWKwv2W)
            - Windows 安装需要配置环境变量 ERLANG_HOME (指向安装目录) 和 Path (添加一条: %ERLANG_HOME%/bin 即可; 环境配完后,使用 CMD 指令 erl -version 验证) 
        +  安装 RabbitMQ Server [Github 下载](https://github.com/rabbitmq/rabbitmq-server) (releases 处有不同版本陈列)
            - Windows 安装需要在安装目录下的 sbin 目录开启 CMD 输入 rabbitmq-plugins enable rabbitmq_management 命令启动管理页面的插件
            - 插件安装完成后再在同目录下找到并打开 rabbitmq-server.bat 启动脚本
                > 之后可在服务管理页面看到 RabbitMQ 正在运行
            - 输入 http://localhost:15672 打开可视化页面
                > 默认账号密码: guest/guest
    + 使用:
        - 创建 SpringBoot 项目 
        - 引入依赖 `spring-boot-starter-amqp`
        - 写配置 
        ```yaml
        spring:
            rabbitmq:
                host: # 主机ip
                post: # 默认 5672
                username: guset 
                password: guest # 默认 admin
        ```
            
