package com.learn.redis.dao;

import com.learn.redis.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/5/10-19:42
 */
@Mapper
public interface UserDao {
    List<User> findAll();

    int insert(User user);

    int delete(String name);

    int updateByName(User user);
}
