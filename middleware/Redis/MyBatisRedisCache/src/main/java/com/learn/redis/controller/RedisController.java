package com.learn.redis.controller;

import com.learn.redis.DTO.UserDTO;
import com.learn.redis.entity.User;
import com.learn.redis.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/5/12-11:54
 */

@RestController
@RequestMapping("session")
public class RedisController {
    @Resource
    private UserService service;

    @GetMapping("getSession")
    public void getSession(@RequestParam String name, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<User> users = service.getByName(name);
        if (users.size() != 0) {
            User u = (User) request.getSession().getAttribute("user");
            if (u == null) {
                u = new User(users.get(0).getName(), users.get(0).getAge(), users.get(0).getJob());
                request.getSession().setAttribute("user", u);
            }
            if (service.getByName(u.getName()).size() == 0) {
                service.insert(u);
            }
            response.getWriter().println("UsrName:" + u.getName() + ", Age:" + u.getAge() + ", Job:" + u.getJob());
        } else {
            response.getWriter().println("Can`t logging.");
        }
    }

    @PostMapping("register")
    public void register(@RequestBody UserDTO user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<User> byName = service.getByName(user.getName());
        if (byName.size() == 0) {
            User u = new User(user.getName(), user.getAge(), user.getJob());
            service.insert(u);
            request.getSession().setAttribute("user", u);
            response.getWriter().println("Successful!");
            response.getWriter().println("UsrName:" + u.getName() + ", Age:" + u.getAge() + ", Job:" + u.getJob());
        } else {
            response.getWriter().println("User already register.");
        }
    }

    @PostMapping("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User u = (User) request.getSession().getAttribute("user");
        if (u != null) {
            response.getWriter().println("UsrName:" + u.getName() + ", Age:" + u.getAge() + ", Job:" + u.getJob());
            request.getSession().invalidate();
            response.getWriter().println("Successful!");
        } else {
            response.getWriter().println("Session is Null");
        }

    }

    @PostMapping("delete")
    public void delete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.getWriter().println("Session is Null");
        } else {
            service.delete(u.getName());
            request.getSession().invalidate();
            response.getWriter().println("Successful!");
        }
    }

}
