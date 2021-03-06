# BloomFilter 布隆过滤器

> 实质: 二进制向量(位数组) + 一系列随机映射函数(哈希函数)
>
> 主要功能: 判断一个元素是否在一个集合中存在
>
> 判断一个元素是否在一个集合中存在, 第一眼思想即为保存在 set 集合之中, 但是数据量一大, 即使有索引, 检索速度也将降低, 存储的空间也将线性增长.
>
> 优点: 大量节省空间
>
> 缺点: 有极小的误判率
>
> 以误判率换取空间(虽然有误判率,但是在处理大数据面前,几乎可以忽略)
>
> 参考: [布隆过滤器，这一篇给你讲的明明白白-阿里云开发者社区 (aliyun.com)](https://developer.aliyun.com/article/773205#slide-0)

## hash 函数

>  任意大小的输入 ==hash 函数==>  特定大小的输出(哈希值/哈希码/散列值)

+ 特征 
  1. **确定性**: hash 值不同, 原始输入数据也不同 (单项 hash 函数)
  2. **哈希碰撞/散列碰撞**: hash 函数的输入和输出并不是唯一对应的关系. 输入相同, 输出可能相同, 也可能不同.
+ 使用 hash 表存储数据仍有空间效率低下的问题, 且只有一个 hash 函数时, 容易产生**哈希碰撞**

## bloom 过滤器数据结构

+ BloomFilter = 固定大小的二进制向量/位图 + 一系列映射函数

+ 变换流程

  1. 初始状态: 长度为 n 的位数组(每个元素只可能时 0, 1 两个值), 位数组的所有位都设为 0

     > 初始状态 9 位的 bloom 位数组: [0, 0, 0, 0, 0, 0, 0, 0, 0]

  2. 变量加入集合中时, 通过 k 条映射函数将加入的变量映射到位数组/位图的 k 个点, 置对应点值为 1.

     > 变量1通过 3 条哈希函数, 将 bloom 位数组的 2, 3, 6 位**置为1**
     >
     > 变量2通过 3 条哈希函数, 将 bloom 位数组的 1, 3, 7 位**置为1**
     >
     > 最终 bloom 位数组: [0, 1, 1, 1, 0, 0, 1, 1, 0]
     >
     > 可见 3 位 被共享置 1.

  3. 查询是否含有某个元素, 则通过对应的点位是否置为 1 即可.

     + 对应点有一个为 0, 则证明查询变量*不存在*

     + 对应点全为1, 则证明查询变量**有可能存在**

       > 映射函数为 hash 函数, 有可能产生哈希碰撞

+ 误判率

  + 原因: 相同 bit 位被多次映射置 1,因此产生了误判.

    > 多个输入经过哈希函数后, 可能对同一位**置为 1**, 这就无法判断是哪个输入产生的 1.

  + 这也使 bloom 过滤器的删除产生问题: bloom filter 的每一位都不一定是一个变量独占的, 可能有多个变量对某一位**置 1**, 因此不能直接删除.

    > 若直接删除一位, 可能会影响到多个输入变量

+ 特征

  1. 一个输入变量/元素被判断为存在时**不一定存在**, 但被判断为不存在时则**一定不存在**
  2. bloom filter 可添加元素/输入变量, 但不能删除元素/输入变量(删除元素, 增加误判率)

## 添加元素和查询元素

+ 添加
  1. 将需要添加的元素给 k 个哈希函数变换
  2. 得到对应于位数组上的 k 个位置
  3. 将位数组上的 k 个点位**置为1**.
+ 查询
  1. 将需要查询的函数给 k 个哈希函数变换
  2. 得到对应于位数组上的 k 个位置
  3. 若 k 个位置中有一个为 0, 则肯定不在集合当中
  4. 若 k 个位置中全是 1, 则可能存在于集合当中

## Bloom Filter 的优缺点

### 优点

1. 节省空间和时间

   > 插入和查询的时间复杂度都为: O(k) 即取决于使用几个哈希函数

2. 哈希函数之间相互没有关系, 方便硬件并行实现

3. BloomFilter **本身不存储元素**, 对于某些保密要求高的场合适配性好

4. BloomFilter 可表示**全集**

### 缺点

1. 随着存入元素的增多, 误算率也随之增高

   > 但元素少的情况,使用 hash 表即可

2. 一般情况不能从 bloomfilter 中删除元素

## Bloom Filter 的应用场景

> 网页 URL 去重, 垃圾邮件的识别, 大集合中重复元素的判断, 缓存穿透...

1. 数据库防止穿库: 使用 BloomFilter 来减少不存在的行或列的查找, 避免**代价高昂的磁盘查找**将会提高数据库查询操作的性能

   > example: Google BigTable , HBase , Cassandra & Postgresql

2. 判断用户是否阅读过某项内容(视频/文章...), 虽然有误判, 但不会让用户看到重复的内容(没看过的肯定不存在于 BloomFilter 的记录中)

3. 缓存雪崩 缓存穿透

   + 一般情况, 判断元素是否在缓存中, 在则返回结果, 不在则访问数据库. 若来一波冷数据, 将产生大量缓存穿透, 严重则将造成缓存雪崩.
   + 使用 BloomFilter, 只有在过滤器中的才会去查询缓存, 查询缓存没找到, 将访问数据库; 若不在过滤器中, 则直接返回.

4. WEB 拦截器: 拦截相同的请求, 防止被重复攻击.用户第一次请求, 可将请求参数加入 BloomFilter 中, 下次访问时, 先判断请求参数是否被过滤器命中.

   > Squid 网页代理缓存服务器在 cache digests 中就使用了布隆过滤器。
   >
   > Google Chrome 浏览器使用了布隆过滤器加速安全浏览服务

## Redis 使用 BloomFilter 插件

1. Docker 安装

   + 镜像名: redislabs/rebloom

   + 常用命令

     ```bash
     bf.add key value # 添加
     bf.exists key value # 判断是否存在
     bf.madd key value [value ...]# 批量添加
     bf.mexists key value [value ...]# 批量判断
     bf.reserve # 自定义 BloomFilter 的三个参数: key(指定存放元素的 key), error_rate(错误率), initial_size(预计存放的元素数量)
     # 设定的 key 若已存在, bf.reserve 的执行将报错.
     # 存放数量超过 initial_size 设定值, 错误率仍会升高.
     ```

     > 如果要使用**自定义的布隆过滤器**需要在 add 操作之前, 使用 bf.reserve 命令**显式地**创建 key

