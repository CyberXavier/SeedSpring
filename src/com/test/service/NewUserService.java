package com.test.service;

import com.minis.batis.SqlSession;
import com.minis.batis.SqlSessionFactory;
import com.minis.beans.factory.annotation.Autowired;
import com.test.entity.User;

import java.sql.ResultSet;

public class NewUserService {
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public User getUserInfo(int userId){
        String sqlid = "com.test.entity.User.getUserInfo";
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return (User) sqlSession.selectOne(sqlid, new Object[]{new Integer(userId)},
                    pstmt -> {
                        ResultSet rs = pstmt.executeQuery();
                        User rtnUser = null;
                        if (rs.next()) {
                            rtnUser = new User();
                            rtnUser.setId(rs.getInt("id"));
                            rtnUser.setName(rs.getString("name"));
                            rtnUser.setBirthday(new java.util.Date(rs.getDate("birthday").getTime()));
                        }
                        return rtnUser;
                    }
                );
    }

    public Integer updateUserInfo(int userId){
        String sqlid = "com.test.entity.User.updateUserInfo";
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Object[] args = new Object[2];
        args[0] = "test1";
        args[1] = userId;
        Integer res = sqlSession.update(sqlid, args);
        return res;
    }

    public Integer insertUserInfo(User user){
        String sqlid = "com.test.entity.User.insertUserInfo";
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Object[] args = new Object[3];
        args[0] = user.getId();
        args[1] = user.getName();
        args[2] = user.getBirthday();
        Integer res = sqlSession.insert(sqlid, args);
        return res;
    }

    public Integer deleteUserInfo(int userId){
        String sqlid = "com.test.entity.User.deleteUserInfo";
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Integer res = sqlSession.delete(sqlid, new Object[]{new Integer(userId)});
        return res;
    }

}
