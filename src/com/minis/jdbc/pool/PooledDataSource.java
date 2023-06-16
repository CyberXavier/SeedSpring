package com.minis.jdbc.pool;

import com.minis.exception.PoolExhaustedException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource {

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int initialSize = 2; // 初始化连接数
    private Properties connectionProperties;
    private int maxIdle = 5; // 默认最大空闲连接数：5
    private int maxBusy = 5; // 默认最大连接数：5
    private long maxWait = 2 * 1000L; //默认2秒最大等待时间

    public BlockingQueue<PooledConnection> busy;
    public BlockingQueue<PooledConnection> idle;

    public PooledDataSource(){}

    private void initPool(){
        this.idle = new ArrayBlockingQueue<>(maxIdle);
        this.busy = new ArrayBlockingQueue<>(maxBusy);

        try {
            for (int i = 0; i < initialSize; i ++) {
                Connection connection = DriverManager.getConnection(url, username, password);
                PooledConnection pooledConnection = new PooledConnection(connection, this);
                this.idle.offer(pooledConnection);
                System.out.println("********add connection pool*********");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnectionFromDriver(getUsername(), getPassword());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnectionFromDriver(getUsername(), getPassword());
    }

    private Connection getConnectionFromDriver(String username, String password) {
        Properties mergedProps = new Properties();
        Properties connProps = getConnectionProperties();
        if (connProps != null) {
            mergedProps.putAll(connProps);
        }
        if (username != null) {
            mergedProps.put("user", username);
        }
        if (username != null) {
            mergedProps.put("password", password);
        }

        if (this.idle == null) {
            initPool();
        }
        // 一直等待直到拿到连接
        PooledConnection pooledConnection = getAvailableConnection();

        return pooledConnection;
    }

    private PooledConnection getAvailableConnection() {
        PooledConnection pooledConnection = null;
        try {
            pooledConnection = this.idle.poll(maxWait, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (pooledConnection == null) {
            throw new PoolExhaustedException("Timeout: Unable to fetch a connection in " +
                    (maxWait / 1000) + " seconds.");
        }
        this.busy.offer(pooledConnection);
        return this.busy.peek();
    }

    protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
        return DriverManager.getConnection(url, props);
    }

    public String getDriverClassName() {
        return driverClassName;
    }
    // 从xml文件中设置驱动名时就加载驱动
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        try {
            Class.forName(this.driverClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not load JDBC driver class [" + driverClassName + "]", e);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxBusy() {
        return maxBusy;
    }

    public void setMaxBusy(int maxBusy) {
        this.maxBusy = maxBusy;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        this.connectionProperties = connectionProperties;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
