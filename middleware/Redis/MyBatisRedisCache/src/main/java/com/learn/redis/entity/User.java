package com.learn.redis.entity;

import java.io.Serializable;

/**
 * @author VHBin
 * @date 2022/5/10-19:40
 */

public class User implements Serializable {
    private String name;
    private Integer age;
    private String job;

    public User() {
    }

    public User(String name, Integer age, String job) {
        this.name = name;
        this.age = age;
        this.job = job;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", job='" + job + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
