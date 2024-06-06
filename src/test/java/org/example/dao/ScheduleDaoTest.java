package org.example.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.dao.api.IScheduleDao;
import org.example.dao.api.IUserDao;
import org.example.dao.ds.DataBaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.Statement;

class ScheduleDaoTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    IUserDao userDao;

    IScheduleDao scheduleDao;

    @BeforeAll
    static void beforeAll() {
        postgres.withInitScript("ddl/0_init.sql");
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setMaximumPoolSize(2);
        DataBaseConnection dataBaseConnection = new DataBaseConnection(new HikariDataSource(config));
//        userDao = new UserDao(new SupplyDao(dataBaseConnection), dataBaseConnection);
        scheduleDao = new ScheduleDao(userDao, dataBaseConnection);
        try (Connection connection = dataBaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE app.users RESTART IDENTITY CASCADE");
            statement.execute("TRUNCATE TABLE app.schedule RESTART IDENTITY CASCADE");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка очищения базы данных", e);
        }
    }

    @Test
    void shouldGetProperly() {

    }

    @Test
    void testGet() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}