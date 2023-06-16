package com.test.service;

import com.minis.batis.SqlSessionFactory;
import com.minis.beans.factory.annotation.Autowired;
import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.RowMapper;
import com.minis.web.bind.annotation.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;
import com.test.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    public List<User> getUsers(int userId){
        final String sql = "select id, name, birthday from users where id>?";
        return (List<User>) jdbcTemplate.query(sql, new Object[]{new Integer(userId)},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User rtnUser = new User();
                        rtnUser.setId(rs.getInt("id"));
                        rtnUser.setName(rs.getString("name"));
                        rtnUser.setBirthday(new java.util.Date(rs.getDate("birthday").getTime()));

                        return rtnUser;
                    }
                }
        );
    }

    public int updateUserInfo(int userId){
        final String sql = "update users set name = ? where id = ?";
        Object[] args = new Object[2];
        args[0] = "xavier";
        args[1] = 1;
        int result = (int) jdbcTemplate.update(sql, args);
        return result;
    }
}
