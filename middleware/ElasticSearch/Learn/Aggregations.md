# Aggregate

> 聚合
>
> 官方文档: [Aggregations | Elasticsearch Guide \[8.1\] | Elastic](https://www.elastic.co/guide/en/elasticsearch/reference/8.1/search-aggregations.html)
>
> 未查看的文档(版本老旧, 但概念应该不变): [3.6.Aggregations(聚合分析) - Elasticsearch 高手之路 (gitbook.io)](https://xiaoxiami.gitbook.io/elasticsearch/ji-chu/36aggregationsju-he-fen-679029)

## 主要概念

> SQL 内, GROUP BY 需要 统计方法才好观察效果
>
> SQL 的 GROUP BY 为 统计方法 提供了范围.

+ 桶 buckets

  > 类似 SQL 分组 (GROUP BY)

  + 桶满足特定条件的文档集合 

    > 桶可以嵌套在其他桶内

  + 分桶的目的: 提供了一种给**文档分组**的方法来计算指标

  + **个人理解**: 桶即为组别, 按照指定的字段, 对比其值是否符合规定要求进行分组.组别内可以再细分.

+ 指标 metrics

  > 类似 SQL 统计方法 (COUNT(), SUM(), MAX() ...)

  + 对桶内的文档进行统计计算

    > 大多数是简单的数学运算 (最小值 平均值 最大值 汇总...)

+ 聚合 Aggregations

  + 桶和指标的组合

    > 可能 只有桶 或 只有指标 或 两者都有
    >
    > e.g.
    >
    > 求 全世界 不同国家 不同性别 不同年龄段 的 平均薪资
    >
    > 1. 世界 > 国家桶
    > 2. 国家桶 > 性别桶
    > 3. 性别桶 > 年龄桶
    > 4. 计算平均薪资
    >
    > 一次请求, 所有数据只遍历一遍.