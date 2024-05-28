package org.example.dao.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseConnectionFactory {

    private static final HikariConfig config = new HikariConfig("datasource.properties");

    private static HikariDataSource ds;

    static {
        ds = new HikariDataSource(config);
    }

    private DataBaseConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
