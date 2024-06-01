package org.example.dao.ds;

import com.zaxxer.hikari.HikariDataSource;
import org.example.dao.api.IDataBaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class DataBaseConnection implements IDataBaseConnection {

    private final HikariDataSource ds;

    public DataBaseConnection(HikariDataSource ds) {
        this.ds = ds;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
