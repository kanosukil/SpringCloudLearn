package com.learn.jedis;

import redis.clients.jedis.Jedis;

public class ConnectUtil {
    private static final String host = "172.22.214.32";
    private static final Integer port = 6379;
    private static final String password = "13131CAHlhb";

    public static Jedis getClient() {
        return getClient(host, port, password);
    }

    public static Jedis defaultConf() {
        Jedis client = ConnectUtil.getClient();
        System.out.printf("Ping: %s\n", client.ping());
        System.out.printf("Select 1: %s\n", client.select(1));
        System.out.printf("Keys *: %s\n", client.keys("*"));
        System.out.printf("Flushdb: %s\n", client.flushDB());
        return client;
    }

    public static Jedis getClient(String host, Integer port) {
        return new Jedis(host, port);
    }

    public static Jedis getClient(String host, Integer port, String password) {
        Jedis client = new Jedis(host, port);
        System.out.printf("Auth: %s\n", client.auth(password));
        return client;
    }

    public static void close(Jedis client) {
        client.close();
    }
}
