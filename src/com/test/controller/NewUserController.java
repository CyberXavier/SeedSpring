package com.test.controller;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;
import com.minis.web.servlet.ModelAndView;
import com.test.entity.User;
import com.test.service.NewUserService;

import java.util.HashMap;
import java.util.Map;

public class NewUserController {
    @Autowired
    private NewUserService newUserService;

    @RequestMapping("/getNewUserInfo")
    @ResponseBody
    public User getUserInfo(int userId){
        User user = newUserService.getUserInfo(userId);
        return user;
    }

    @RequestMapping("/updateNewUserInfo")
    public ModelAndView updateUserInfo(int userId){
        int res = newUserService.updateUserInfo(userId);
        Map<String, Integer> model = new HashMap<>();
        model.put("msg", res);
        ModelAndView mv = new ModelAndView("test", model);
        return mv;
    }

    @RequestMapping("/insertNewUserInfo")
    public ModelAndView insertUserInfo(User user){
        Integer res = newUserService.insertUserInfo(user);
        Map<String, Integer> model = new HashMap<>();
        model.put("msg", res);
        ModelAndView mv = new ModelAndView("test", model);
        return mv;
    }

    @RequestMapping("/deleteNewUserInfo")
    public ModelAndView deleteUserInfo(int userId){
        int res = newUserService.deleteUserInfo(userId);
        Map<String, Integer> model = new HashMap<>();
        model.put("msg", res);
        ModelAndView mv = new ModelAndView("test", model);
        return mv;
    }

}
