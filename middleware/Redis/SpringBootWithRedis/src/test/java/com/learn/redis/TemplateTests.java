package com.learn.redis;

import com.learn.redis.Entity.Person;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;

@SpringBootTest
public class TemplateTests {
    private static final Logger logger = LoggerFactory.getLogger(TemplateTests.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void templateTest() {
        // 获取链接
        RedisConnection connection = null;
        try {
            // 由于是 Lettuce 切库需要使用 重建链接 的方式
            LettuceConnectionFactory factory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
            factory.setDatabase(1);
            redisTemplate.setConnectionFactory(factory);
            factory.resetConnection();
            connection = factory.getConnection();
            connection.keys("*".getBytes(StandardCharsets.UTF_8));
            connection.flushDb();
            /*
                对不同的数据类型操作
                opsForValue 对 Redis String
                opsForList 对 Redis List
                opsForSet
                opsForZSet
                opsForHash
                opsForGeo
                opsForHyperLoglog
                opsForStream
            */
            redisTemplate.opsForValue().set("name", "VHBin吕"); // 命令行正常显示非英文的内容, 但 Redis 内部为转变后的编码形式
            // 所有操作都需要使用 序列化, 而 SpringData Redis 默认使用的是 JDK 的序列化
            logger.info("Get: {}", redisTemplate.opsForValue().get("name"));
            redisTemplate.delete("name");

        } catch (NullPointerException e) {
            logger.error("NullPointer: ", e);
        } catch (Exception e) {
            logger.error("Exception", e);
        } finally {
            connection.close();
        }

    }

    @Test
    void templateObjectTest() {
        Person person = new Person("VHBin", 21, "学生");
        redisTemplate.opsForValue().set("User", person);
        logger.info("Get: {}", redisTemplate.opsForValue().get("User"));
    }
}
