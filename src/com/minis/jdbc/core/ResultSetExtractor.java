package com.minis.jdbc.core;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetExtractor<T> {
    /**
     * 把 JDBC 返回的 ResultSet 数据集映射为一个集合对象
     * @param rs
     * @return
     * @throws SQLException
     */
    T extractData(ResultSet rs) throws SQLException;
}
