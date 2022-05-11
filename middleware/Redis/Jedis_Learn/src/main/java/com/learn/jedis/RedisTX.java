package com.learn.jedis;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Random;

public class RedisTX {
    public static void main(String[] args) {
        Jedis client = ConnectUtil.defaultConf();
        Transaction multi = client.multi();
        try {
            JSONObject json = new JSONObject();
            json.put("name", "吕海彬");
            json.put("age", "21");
            json.put("job", "学生");
            json.put("爱好", "漫画");
            multi.set("Key1", json.toJSONString());
            multi.incr("Key1");
            int i = new Random().nextInt();
            System.out.println("i=" + i);
            if (i % 2 == 1) {
                throw new Exception("Exception: Death");
            }
            multi.set("key2", "Ops");
            multi.getrange("Key1", 0, -1);
            System.out.println("Exec: " + multi.exec());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Discard: " + multi.discard());
        } finally {
            ConnectUtil.close(client);
        }
    }
}
