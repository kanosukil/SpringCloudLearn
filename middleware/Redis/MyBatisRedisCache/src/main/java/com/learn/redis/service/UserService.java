package com.learn.redis.service;

import com.learn.redis.entity.User;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/5/10-20:03
 */

public interface UserService {
    List<User> findAll();

    int insert(User user);

    int delete(String name);

    int updateByName(User user);

    List<User> getByName(String name);
}
