package com.learn.jedis;

import redis.clients.jedis.Jedis;

public class RedisString {
    public static void main(String[] args) {
        Jedis client = ConnectUtil.defaultConf();
        String key = "VHBin";
        String value = "VHBin is the dog of Kano";
        String append = ", Kano is the Goddess of VHBin";
        String change = "IN";
        System.out.printf("Set key value: %s\n", client.set(key, value));
        System.out.printf("Get key value: %s\n", client.get(key));
        System.out.printf("Append key value: %s\n", client.append(key, append));
        System.out.printf("Strlen key: %s\n", client.strlen(key));
        System.out.printf("Getset key value: %s\n", client.getSet(key, "123"));
        System.out.printf("Incr key: %s\n", client.incr(key));
        System.out.printf("Decr key: %s\n", client.decr(key));
        System.out.printf("Decrby key decrement: %s\n", client.decrBy(key, 23L));
        System.out.printf("Incrby key decrement: %s\n", client.incrBy(key, 23L));
        System.out.printf("Getrange key start end: %s\n", client.getrange(key, 0, 5));
        System.out.printf("Setrange key offset value: %s\n", client.setrange(key, 3, change));
        System.out.printf("Get: %s\n", client.get(key));
        System.out.printf("Setex key second value: %s\n", client.setex(key + "1", 12, change));
        System.out.printf("TTL key: %s\n", client.ttl(key + "1"));
        System.out.printf("Setnx key value: %s\n", client.setnx(key + "1", change + "2"));
        System.out.printf("Del key: %s\n", client.del(key));
        ConnectUtil.close(client);
    }
}
