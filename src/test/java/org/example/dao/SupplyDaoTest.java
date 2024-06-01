package org.example.dao;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

class SupplyDaoTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @Test
    void get() {
    }

    @Test
    void testGet() {
    }

    @Test
    void testGet1() {
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