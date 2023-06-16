package com.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;
import com.minis.web.servlet.ModelAndView;
import com.test.entity.User;
import com.test.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/getUserInfo")
    @ResponseBody
    public User getUserInfo(int userId){
        User userInfo = userService.getUserInfo(userId);
        return userInfo;
    }

    @RequestMapping("/updateUserInfo")
    public ModelAndView updateUserInfo(int userId){
        int res = userService.updateUserInfo(userId);
        Map<String, Integer> map = new HashMap<>();
        map.put("msg", res);
        ModelAndView mv = new ModelAndView("test",map);
        return mv;
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public User getUserByList(int userId){
        List<User> users = userService.getUsers(userId);
        return users.get(0);
    }

    @RequestMapping("/getUserList")
    public ModelAndView getUserList(int userId){
        List<User> users = userService.getUsers(userId);
        Map<String, Object> map = new HashMap<>();
        int i = 1;
        for (User user : users) {
            map.put("user" + i, user.getId());
            i ++;
        }
        ModelAndView mv = new ModelAndView("showUserList", map);
        return mv;
    }

}
