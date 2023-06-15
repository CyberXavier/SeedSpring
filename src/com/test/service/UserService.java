package com.test.service;

import com.minis.beans.factory.annotation.Autowired;
import com.minis.jdbc.core.JdbcTemplate;
import com.test.entity.User;

import java.sql.ResultSet;

public class UserService {

    @Autowired
    JdbcTemplate jdbcTemplate;

//    public User getUserInfo(int userId){
//        String sql = "select id, name, birthday from users where id=" + userId;
//        return (User) jdbcTemplate.query(
//                (stmt)->{
//                    ResultSet rs = stmt.executeQuery(sql);
//                    User rtnUser = null;
//                    if (rs.next()) {
//                        rtnUser = new User();
//                        rtnUser.setId(rs.getInt("id"));
//                        rtnUser.setName(rs.getString("name"));
//                        rtnUser.setBirthday(rs.getDate("birthday"));
//                    }
//                    return rtnUser;
//                }
//        );
//    }

    public User getUserInfo(int userId){
        final String sql = "select id, name, birthday from users where id = ?";
        return (User) jdbcTemplate.query(sql, new Object[]{new Integer(userId)},
                pstmt -> {
                    ResultSet rs = pstmt.executeQuery();
                    User rtnUser = null;
                    if (rs.next()) {
                        rtnUser = new User();
                        rtnUser.setId(rs.getInt("id"));
                        rtnUser.setName(rs.getString("name"));
                        rtnUser.setBirthday(rs.getDate("birthday"));
                    }
                    return rtnUser;
                });
    }

}
