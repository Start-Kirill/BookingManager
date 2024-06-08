package org.example.dao.api;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDataBaseConnection {

    Connection getConnection() throws SQLException;
}
