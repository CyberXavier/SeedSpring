package com.test.service;

import com.minis.jdbc.core.OldJdbcTemplate;
import com.test.entity.User;

public class OldUserService {
    public User getUserInfo(int userId){
        String sql = "select id, name, birthday from users where id=" + userId;
        OldJdbcTemplate oldJdbcTemplate = new UserOldJdbcImpl();
        User rtnUser = (User) oldJdbcTemplate.query(sql);

        return rtnUser;
    }

//    public static void main(String[] args) {
//        UserService userService = new UserService();
//        User userInfo = userService.getUserInfo(1);
//        System.out.println(userInfo.getName());
//        System.out.println(userInfo.getBirthday());
//    }
}
