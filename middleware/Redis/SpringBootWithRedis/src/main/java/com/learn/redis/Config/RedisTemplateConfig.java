package com.learn.redis.Config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisTemplateConfig {
    // 编写自定义的 RedisTemplate
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        // 为开发方便使用 <String, Object> 类型
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 最好先将链接设定设置好再设置其他的设置
        template.setConnectionFactory(redisConnectionFactory);
        // 定义 jackson 的序列化方式
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer
                = new Jackson2JsonRedisSerializer<>(Object.class);
        // 使用 ObjectMapper 进行转义
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        // 定义 String 类型的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置 key 的序列化方式为 string 的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // 设置 hash 的 key 的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // 设置 value 的序列化方式为 json 的
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置 hash 的 value 的序列化方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
