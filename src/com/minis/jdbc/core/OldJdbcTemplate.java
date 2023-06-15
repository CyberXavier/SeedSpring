package com.minis.jdbc.core;

import java.sql.*;

public abstract class OldJdbcTemplate {

    String url = "jdbc:mysql://localhost:3306/mytest";
    String username = "root";
    String password = "12345678";

    public OldJdbcTemplate(){}
    public Object query(String sql){
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object rtnObj = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url,username, password);

            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            // 调用返回数据方法，由程序员自行实现
            rtnObj = doInStatement(rs);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                rs.close();
                stmt.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return rtnObj;
    }

    protected abstract  Object doInStatement(ResultSet rs);

}
