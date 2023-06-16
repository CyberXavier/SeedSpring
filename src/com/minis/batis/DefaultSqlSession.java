package com.minis.batis;

import com.minis.jdbc.core.JdbcTemplate;
import com.minis.jdbc.core.PreparedStatementCallback;
import com.minis.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class DefaultSqlSession implements SqlSession{

    JdbcTemplate jdbcTemplate;
    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    private DataSource readDataSource;
    private DataSource writeDataSource;

    @Override
    public void setReadDataSource(DataSource readDataSource) {
        this.readDataSource = readDataSource;
    }
    @Override
    public void setWriteDataSource(DataSource writeDataSource) {
        this.writeDataSource = writeDataSource;
    }

    public DataSource getReadDataSource() {
        return readDataSource;
    }

    public DataSource getWriteDataSource() {
        return writeDataSource;
    }

    SqlSessionFactory sqlSessionFactory;
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return this.sqlSessionFactory;
    }


    @Override
    public Object selectOne(String sqlid, Object[] args, PreparedStatementCallback pstmtcallback) {
        MapperNode mapperNode = this.getSqlSessionFactory().getMapperNode(sqlid);
        String sqlType = mapperNode.getSqlType();
        String sql = mapperNode.getSql();
        if ("0".equals(sqlType)) { //表示read
            jdbcTemplate.setDataSource(this.readDataSource);
        }
        return jdbcTemplate.query(sql, args, pstmtcallback);
    }

    @Override
    public List<Object> select(String sqlid, Object[] args, RowMapper<Object> rowMapper) {
        MapperNode mapperNode = this.getSqlSessionFactory().getMapperNode(sqlid);
        String sqlType = mapperNode.getSqlType();
        String sql = mapperNode.getSql();
        if ("0".equals(sqlType)) { //表示read
            jdbcTemplate.setDataSource(this.readDataSource);
        }
        return jdbcTemplate.query(sql, args, rowMapper);
    }

    @Override
    public Integer update(String sqlid, Object[] args) {
        MapperNode mapperNode = this.getSqlSessionFactory().getMapperNode(sqlid);
        String sqlType = mapperNode.getSqlType();
        String sql = mapperNode.getSql();
        if ("1".equals(sqlType)) { //表示read
            jdbcTemplate.setDataSource(this.writeDataSource);
        }
        return jdbcTemplate.update(sql, args);
    }

    @Override
    public Integer insert(String sqlid, Object[] args) {
        MapperNode mapperNode = this.getSqlSessionFactory().getMapperNode(sqlid);
        String sqlType = mapperNode.getSqlType();
        String sql = mapperNode.getSql();
        if ("2".equals(sqlType)) { //表示read
            jdbcTemplate.setDataSource(this.writeDataSource);
        }
        return jdbcTemplate.insert(sql, args);
    }

    @Override
    public Integer delete(String sqlid, Object[] args) {
        MapperNode mapperNode = this.getSqlSessionFactory().getMapperNode(sqlid);
        String sqlType = mapperNode.getSqlType();
        String sql = mapperNode.getSql();
        if ("3".equals(sqlType)) { //表示read
            jdbcTemplate.setDataSource(this.writeDataSource);
        }
        return jdbcTemplate.delete(sql, args);
    }
}
