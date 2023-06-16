package com.minis.batis;

import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.PreparedStatementCallback;
import com.minis.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public interface SqlSession {
    void setJdbcTemplate(JdbcTemplate jdbcTemplate);
    void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory);
    void setReadDataSource(DataSource readDataSource);
    void setWriteDataSource(DataSource writeDataSource);
    Object selectOne(String sqlid, Object[] args, PreparedStatementCallback pstmtcallback);
    List<Object> select(String sqlid, Object[] args, RowMapper<Object> rowMapper);
    Integer update(String sqlid, Object[] args);
    Integer insert(String sqlid, Object[] args);
    Integer delete(String sqlid, Object[] args);
}
