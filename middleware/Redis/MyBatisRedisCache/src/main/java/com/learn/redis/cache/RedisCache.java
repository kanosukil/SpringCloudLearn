package com.learn.redis.cache;

import com.learn.redis.Utils.ApplicationContextUtils;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * @author VHBin
 * @date 2022/5/10-20:30
 */

public class RedisCache implements Cache {
    private final String id;

    public RedisCache(String id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    private RedisTemplate<String, Object> getRedisTemplate() {
        return (RedisTemplate<String, Object>) ApplicationContextUtils.getBean("redisTemplate");
    }

    private String getMD5Key(String s) {
        String s1 = DigestUtils.md5DigestAsHex(s.getBytes(StandardCharsets.UTF_8));
        System.out.println(s1);
        return s1;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object o, Object o1) {
        RedisTemplate<String, Object> template = getRedisTemplate();
        template.opsForHash().put(id, getMD5Key(o.toString()), o1);
        template.expire(id, 30, TimeUnit.MINUTES); // 太过于粗糙, 将导致全部缓存同一时间, 全部失效, 可能引发缓存穿透或者缓存雪崩
    }

    @Override
    public Object getObject(Object o) {
        return getRedisTemplate().opsForHash().get(id, getMD5Key(o.toString()));
    }

    @Override
    public Object removeObject(Object o) {
        System.out.println("================================removeObject===================================");
        return null;
    }

    @Override
    public void clear() {
        System.out.println("====================================clear======================================");
        getRedisTemplate().delete(id);
    }

    @Override
    public int getSize() {
        return getRedisTemplate().opsForHash().size(id).intValue();
    }
}
