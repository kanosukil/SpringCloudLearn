package com.learn.redis.service.impl;

import com.learn.redis.entity.User;
import com.learn.redis.service.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author VHBin
 * @date 2022/5/10-20:15
 */
@SpringBootTest
class UserServiceImplTest {
    private final Logger log = LoggerFactory.getLogger(UserServiceImplTest.class);
    @Autowired
    private UserService service;

    @Test
    void doTest() {
        log.info("Res: {}", service.insert(new User("VHBin", 21, "Student")));
        log.info("Res: {}", service.insert(new User("Kano", 18, "Singer")));
    }

    @Test
    void delete() {
        log.info("Res: {}", service.delete("VHBin"));
    }

    @Test
    void find() {
        service.findAll().forEach(e -> log.info("User -> {}", e));
        log.info("----------------------------------------------------------------------------");
        service.findAll().forEach(e -> log.info("User -> {}", e));
    }

    @Test
    void update() {
        service.updateByName(new User("VHBin", 22, "Dog"));
    }
}