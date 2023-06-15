package com.minis.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementCallback {
    Object doInPreparedStatement(PreparedStatement pstmt) throws SQLException;
}
