package com.test.service;

import com.minis.jdbc.core.OldJdbcTemplate;
import com.test.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserOldJdbcImpl extends OldJdbcTemplate {
    @Override
    protected Object doInStatement(ResultSet rs) {
        User rtnUser = null;
        try {
            if (rs.next()) {
                rtnUser = new User();
                rtnUser.setId(rs.getInt("id"));
                rtnUser.setName(rs.getString("name"));
                rtnUser.setBirthday(rs.getDate("birthday"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return rtnUser;
    }
}
