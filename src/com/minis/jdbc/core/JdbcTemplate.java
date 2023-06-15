package com.minis.jdbc.core;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class JdbcTemplate {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource(){
        return this.dataSource;
    }

    public JdbcTemplate(){}

    public Object query(StatementCallback stmtcallback){
        Connection con = null;
        Statement stmt = null;

        try {
            con = dataSource.getConnection();

            stmt = con.createStatement();

            return stmtcallback.doInStatement(stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                stmt.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object query(String sql, Object[] args, PreparedStatementCallback pstmtcallback){
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = dataSource.getConnection();

            pstmt = con.prepareStatement(sql);
            //设置参数
            ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
            argumentSetter.setValues(pstmt);

            return pstmtcallback.doInPreparedStatement(pstmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                pstmt.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper){
        RowMapperResultSetExtractor<T> resultExtractor = new RowMapperResultSetExtractor<>(rowMapper);
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = dataSource.getConnection();

            pstmt = con.prepareStatement(sql);
            ArgumentPreparedStatementSetter argumentSetter = new ArgumentPreparedStatementSetter(args);
            argumentSetter.setValues(pstmt);
            rs = pstmt.executeQuery();

            return resultExtractor.extractData(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                pstmt.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
