package com.learn.redis.service.impl;

import com.learn.redis.dao.UserDao;
import com.learn.redis.entity.User;
import com.learn.redis.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/5/10-20:06
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int insert(User user) {
        return userDao.insert(user);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int delete(String name) {
        return userDao.delete(name);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateByName(User user) {
        return userDao.updateByName(user);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<User> getByName(String name) {
        return userDao.getByName(name);
    }
}
