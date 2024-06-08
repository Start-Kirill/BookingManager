package org.example.dao.factory.ds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.dao.api.IDataBaseConnection;
import org.example.dao.ds.DataBaseConnection;

public class DataBaseConnectionFactory {

    private static final HikariConfig config = new HikariConfig("/datasource.properties");

    private static HikariDataSource ds;

    static {
        ds = new HikariDataSource(config);
    }

    private static volatile IDataBaseConnection instance;

    private DataBaseConnectionFactory() {
    }

    public static IDataBaseConnection getInstance() {
        if (instance == null) {
            synchronized (DataBaseConnectionFactory.class) {
                if (instance == null) {
                    instance = new DataBaseConnection(ds);
                }
            }
        }
        return instance;
    }
}
