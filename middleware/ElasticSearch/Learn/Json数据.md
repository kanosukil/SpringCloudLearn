# Jackson

> Maven 依赖: 
>
> ```xml
> <dependency>
>     <groupId>com.fasterxml.jackson.core</groupId>
>     <artifactId>jackson-databind</artifactId>
>     <version>{版本号}</version>
> </dependency>
> ```
>
> jackson-databind 包括对应版本的 jackson-core & jackson-annotations (至少 2.12.3 包括)

## 简单使用

1. 创建 ObjectMapper 对象

   `ObjectMapper mapper = new ObjectMapper();`

2. 反序列化 Json -> 对象

   `Student student = mapper.readValue(jsonString, Student.class);`

3. 序列化 对象 -> Json

   `String jsonString = mapper.writeValueAsString(student);`

## 数据绑定

> 数据绑定: 用于 **Json 转换** 和 **使用属性访问**或**使用注解POJO(普通Java对象)**
>
> ObjectMapper 读写 Json 两种类型的数据绑定
>
> 数据绑定最方便的方式: 类 XML 的 JAXB 解析器

+ 类型

  1. 简单数据绑定

     > 从 Java Map , Java List , Java String , Java Number , Java Boolean , null 对象转换到 Json 

  2. 完整数据绑定

     > 转换 Json 到 任何 Java 类型

### 简单数据绑定

+  Java 核心数据类型 映射 Json 数据类型 (写 Json)

  | Json 类型         | Java 类型                       |
  | ----------------- | ------------------------------- |
  | object            | `LinkedHashMap<String, Object>` |
  | array             | `ArrayList<Object>`             |
  | string            | `String`                        |
  | complete number   | `Integer Long / BigInteger`     |
  | fractional number | `Double / BigDecimal`           |
  | true \| false     | `Boolean`                       |
  | null              | `null`                          |

### 完整数据绑定

+ 读 Json

  `Object o = mapper.readValue(jsonString, Object.class)`

  > 反序列化 Json 到 List
  >
  > `  List<Budget> budgets = mapper.readValue(jsonString, new TypeReference<List<Budget>>() { });`

## Json 树模型

+ 以树的形式存储 Json 数据

```java
public static void main(String args[]) 
    throws JsonProcessingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{" +
        "\"name\":\"Mahesh Kumar\"," +
        " \"age\":21," +
        "\"verified\":false," +
        "\"marks\": [100,90,85]" +
        "}";
    // 从 JSON数据 创建 树
    JsonNode rootNode = mapper.readTree(jsonString);
    // 使用相对路径从根节点遍历树, 并处理从该节点得到的每个子节点
    // 对应 "name": "Mahesh Kumar"
    JsonNode nameNode = rootNode.path("name");
    System.out.println("Name: " + nameNode.textValue());
    // 对应 "age": 21
    JsonNode ageNode = rootNode.path("age");
    System.out.println("Age: " + ageNode.intValue());
	// 对应 "verified": false
    JsonNode verifiedNode = rootNode.path("verified");
    System.out.println("Verified: " + (verifiedNode.booleanValue() ? "Yes" : "No"));
	// 对应 "marks": [100,90,85]
    JsonNode marksNode = rootNode.path("marks");
    Iterator<JsonNode> iterator = marksNode.elements();
    System.out.print("Marks: [ ");
    while (iterator.hasNext()) {
        JsonNode marks = iterator.next();
        System.out.print(marks.intValue() + " ");
    }
    System.out.println("]");
}
```

+ 将 树模型 转换到 Java 对象

```java
public static void main(String args[]) 
    throws JsonGenerationException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{" +
        "\"name\":\"Mahesh\"," +
        " \"age\":21" +
        "}";
    // 从 JSON数据 创建 树
    JsonNode rootNode = mapper.readTree(jsonString);
    // 将 数 转换成 Java 对象
    Student student = mapper.treeToValue(rootNode, Student.class);
    System.out.println(student);
}
```



## Jackson 流式 API

> 开销最低, 读写最快
>
> 类似 XML 的 Stax 解析器

+ JsonGenerator : 写入 Json 字符串
+ JsonParser : 解析 Json 字符串 (读取)
+ 两者 都需要 JsonFactory 创建
  + `new JsonFactory().createJsonGenerator(new File("file-name.json"), JsonEncoding.UTF8);`
  + `new JsonFactory().createJsonParser(new File("file-name.json"));`

# FastJson

> Java 库
>
> Java Object -> Json || Json ->Java Object

+ 特征
  1. 服务器端 & 安卓客户端
  2. 允许转换预先存在的无法修改的对象 (只有.class字节码, 没有.java源码)
  2. 支持 Java 泛型
  2. 允许对象的自定义表示 & 自定义序列化类
  2. 支持任意复杂对象

> Maven 依赖
>
> ```xml
> <dependency>
>     <groupId>com.alibaba</groupId>
>     <artifactId>fastjson</artifactId>
>     <version>{版本号}</version>
> </dependency>
> ```

## 简单使用

+ toJSONString() : 对象 -> JSON 字符串

  ```java
  String jsonString = JSON.toJSONString(Object); // Object 可为 List<对象> 或 单个对象
  // BeanToArray
  String jsonString = JSON.toJSONString(List<Bean>, SerializerFeature.BeanToArray);
  // 输出的为一个 Json Array: 没有 key, 只有 value
  ```

  + 创建 JSON 对象

    + JSONObject (FastJson 提供) : 可当作 `Map<String, Object>`

    + JSONArray (FastJson 提供) : 可当作 `List<Object>` 

    ```java
    JSONArray jsonArray = new JSONArray();
    for (int i = 0; i < 2; i++) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("AGE", 10);
        jsonObject.put("FULL NAME", "Doe " + i);
        jsonObject.put("DATE OF BIRTH", "2016/12/12 12:12:12");
        jsonArray.add(jsonObject);
    }
    String jsonOutput = jsonArray.toJSONString();
    ```

+ parseObject() : JSON 字符串 -> 对象

  > 反序列化时的对象, 必须要有默认无参的构造函数, 否则会报异常.

  ```java
  Object o = JSON.parseObject(jsonString, Object.class);
  ```

+ 可在 对象 定义时 添加 注解 `@JSONField`

  > **注意：**FastJson 在进行操作时，是根据 getter 和 setter 的方法进行的，并不是依据 Field 进行。
  >
  > **注意：**若属性是私有的，必须有 set 方法。否则无法反序列化。

  ```Java
  @JSONField(name="自定义 JSON name", serialize=false||true, deserialize=false||true, ordinal = int, format = "格式化 date 属性: dd/MM/yy") 
  // serialize: 是否序列化(即加入到 Json 数据中, false 则表示该属性不在 Json 数据中显示) 
  // deserialize: 是否反序列化
  // ordinal: 指定字段顺序 
  // format: 配置日期格式化 e.g. yyyyMMdd  dd/MM/yy
  ```
  
  + 添加在 getter/setter 上
  
    ```java
    public class A {
          private int id;
          @JSONField(name="ID")
          public int getId() {return id;}
          @JSONField(name="ID")
          public void setId(int value) {this.id = id;}
    }
    ```
  
  + 添加在 field 上
  
    ```java
     public class A {
          @JSONField(name="ID")
          private int id;
          public int getId() {return id;}
          public void setId(int value) {this.id = id;}
     }
    ```

+ 配置 Json 转化 : ContextValueFiter

  + fastjson-1.2.9 以上

    ```java
    ContextValueFilter valueFilter = new ContextValueFilter () {
        public Object process(
            BeanContext context, Object object, String name, Object value) {
            if (name.equals("自订值")) {
                // 自订操作
                return "NOT TO DISCLOSE"; // 反映在 输出的 JsonString 中, 匹配的对应位置为 return 的值
            }
            if (value.equals("自订值")) {
                return ((String) value).toUpperCase();
            } else {
                return null;
            }
        }
    };
    String jsonOutput = JSON.toJSONString(listOfPersons, valueFilter);
    ```

+ NameFilter & SerializeConfig

  > **NameFilter**: 序列化时修改 Key
  >
  > **SerializeConfig**：内部是个map容器 主要功能是配置并记录每种Java类型对应的序列化类

  ```java
  NameFilter formatName = new NameFilter() {
      public String process(Object object, String name, Object value) {
          return name.toLowerCase().replace(" ", "_"); // 返回值对应 Json 数据的 key/name 值
      }
  };
  SerializeConfig.getGlobalInstance().addFilter(Object.class,  formatName); // 将创建的 NameFilter 添加到全局实例中(NameFilte 属于 SerializeConfig 中的 J).
  String jsonOutput =
      JSON.toJSONStringWithDateFormat(listOfPersons, "yyyy-MM-dd"); // 快速格式化信息
  ```

  