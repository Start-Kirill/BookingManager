package org.example.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.core.exceptions.NullArgumentException;
import org.example.dao.ds.DataBaseConnection;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


class SupplyDaoTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    Supply supplyOne;
    Supply supplyTwo;

    User master;

    SupplyDao supplyDao;

    UserDao userDao;

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
        supplyDao = new SupplyDao(dataBaseConnection);
        userDao = new UserDao(dataBaseConnection);
        try (Connection connection = dataBaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE app.supply RESTART IDENTITY CASCADE");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка очищения базы данных", e);
        }

        master = new User(UUID.randomUUID(), "Kirill", "+123456789", UserRole.MASTER, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        userDao.save(master);
        supplyOne = new Supply(UUID.randomUUID(), "cut", new BigDecimal("50.50"), 100, List.of(master), LocalDateTime.now(), LocalDateTime.now());
        supplyTwo = new Supply(UUID.randomUUID(), "non-cut", new BigDecimal("150.50"), 150, List.of(master), LocalDateTime.now(), LocalDateTime.now());
    }


    @Test
    void shouldGetProperly() {
        supplyDao.save(supplyOne);
        User updatedMaster = this.userDao.get(master.getUuid()).orElseThrow();
        supplyTwo.setMasters(List.of(updatedMaster));
        supplyDao.save(supplyTwo);

        List<Supply> supplies = this.supplyDao.get();

        assertEquals(2, supplies.size());
    }

    @Test
    void shouldGetEmpty() {
        List<Supply> supplies = this.supplyDao.get();

        assertEquals(0, supplies.size());
        assertEquals(List.of(), supplies);
    }

    @Test
    void shouldGetByUuidProperly() {
        supplyDao.save(supplyOne);
        User updatedMaster = this.userDao.get(master.getUuid()).orElseThrow();
        supplyTwo.setMasters(List.of(updatedMaster));
        supplyDao.save(supplyTwo);

        Supply supply = this.supplyDao.get(supplyOne.getUuid()).orElseThrow();

        assertEquals(supplyOne, supply);
    }

    @Test
    void shouldThrowWhileGetByWrongUuid() {
        supplyDao.save(supplyOne);
        Optional<Supply> supplyOptional = this.supplyDao.get(UUID.randomUUID());

        assertThrows(NoSuchElementException.class, supplyOptional::orElseThrow);
    }

    @Test
    void shouldThrowWhileGetByNullUuid() {
        supplyDao.save(supplyOne);

        UUID nullUuid = null;
        assertThrows(NullArgumentException.class, () -> this.supplyDao.get(nullUuid));
    }

    @Test
    void shouldSaveProperly() {
        Supply savedOne = supplyDao.save(supplyOne);
        User updatedMaster = this.userDao.get(master.getUuid()).orElseThrow();
        supplyTwo.setMasters(List.of(updatedMaster));
        Supply savedTwo = supplyDao.save(supplyTwo);

        assertEquals(supplyOne, savedOne);
        assertEquals(supplyTwo, savedTwo);
    }

    @Test
    void shouldThrowWhileSaveNull() {
        assertThrows(NullArgumentException.class, () -> supplyDao.save(null));
    }

    @Test
    void shouldThrowWhileSaveWithNullUuidField() {
        supplyOne.setUuid(null);
        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supplyOne));
    }

    @Test
    void shouldThrowWhileSaveWithNullPriceField() {
        supplyOne.setPrice(null);
        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supplyOne));
    }

    @Test
    void shouldThrowWhileSaveWithNullNameField() {
        supplyOne.setName(null);
        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supplyOne));
    }

    @Test
    void shouldThrowWhileSaveWithNullDtCreateField() {
        supplyOne.setDtCreate(null);
        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supplyOne));
    }

    @Test
    void shouldThrowWhileSaveWithNullDtUpdateField() {
        supplyOne.setDtUpdate(null);
        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supplyOne));
    }

    @Test
    void shouldThrowWhileSaveWithExistedName() {
        this.supplyDao.save(supplyOne);
        supplyTwo.setName(supplyOne.getName());
        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supplyTwo));
    }

    @Test
    void shouldUpdateProperly() {
        this.supplyDao.save(supplyOne);
        User updatedMaster = this.userDao.get(master.getUuid()).orElseThrow();
        supplyOne.setMasters(List.of(updatedMaster));

        String newName = "staining";
        BigDecimal newPrice = BigDecimal.valueOf(100.70);
        Integer newDuration = 120;
        supplyOne.setName(newName);
        supplyOne.setPrice(newPrice);
        supplyOne.setDuration(newDuration);
        Supply updated = this.supplyDao.update(supplyOne);

        assertEquals(newName, updated.getName());
        assertEquals(newPrice, updated.getPrice());
        assertEquals(newDuration, updated.getDuration());
        assertEquals(supplyOne.getDtCreate().truncatedTo(ChronoUnit.MILLIS), updated.getDtCreate().truncatedTo(ChronoUnit.MILLIS));
        assertNotEquals(supplyOne.getDtUpdate().truncatedTo(ChronoUnit.MILLIS), updated.getDtUpdate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void shouldUpdateNullDurationProperly() {
        supplyOne = this.supplyDao.save(supplyOne);
        User updatedMaster = this.userDao.get(master.getUuid()).orElseThrow();
        supplyOne.setMasters(List.of(updatedMaster));

        String newName = "staining";
        BigDecimal newPrice = BigDecimal.valueOf(100.70);
        Integer newDuration = null;
        supplyOne.setName(newName);
        supplyOne.setPrice(newPrice);
        supplyOne.setDuration(newDuration);
        Supply updated = this.supplyDao.update(supplyOne);

        assertEquals(newName, updated.getName());
        assertEquals(newPrice, updated.getPrice());
        assertNull(updated.getDuration());
        assertEquals(supplyOne.getDtCreate().truncatedTo(ChronoUnit.MILLIS), updated.getDtCreate().truncatedTo(ChronoUnit.MILLIS));
        assertNotEquals(supplyOne.getDtUpdate().truncatedTo(ChronoUnit.MILLIS), updated.getDtUpdate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void shouldThrowWhileUpdateNull() {
        assertThrows(NullArgumentException.class, () -> this.supplyDao.update(null));
    }

    @Test
    void shouldThrowWhileUpdateWithNullUuidField() {
        this.supplyDao.save(supplyOne);

        String newName = "staining";
        BigDecimal newPrice = BigDecimal.valueOf(100.70);
        Integer newDuration = 120;
        supplyOne.setUuid(null);
        supplyOne.setName(newName);
        supplyOne.setPrice(newPrice);
        supplyOne.setDuration(newDuration);

        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.update(supplyOne));
    }

    @Test
    void shouldThrowWhileUpdateWithNullNameField() {
        this.supplyDao.save(supplyOne);

        String newName = null;
        BigDecimal newPrice = BigDecimal.valueOf(100.70);
        Integer newDuration = 120;
        supplyOne.setName(newName);
        supplyOne.setPrice(newPrice);
        supplyOne.setDuration(newDuration);
        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.update(supplyOne));
    }

    @Test
    void shouldThrowWhileUpdateWithNullPriceField() {
        this.supplyDao.save(supplyOne);

        String newName = "staining";
        BigDecimal newPrice = null;
        Integer newDuration = 120;
        supplyOne.setName(newName);
        supplyOne.setPrice(newPrice);
        supplyOne.setDuration(newDuration);
        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.update(supplyOne));
    }

    @Test
    void shouldThrowWhileUpdateNotUpToDatedObject() {
        this.supplyDao.save(supplyOne);

        String newName = "staining";
        BigDecimal newPrice = BigDecimal.valueOf(100.70);
        Integer newDuration = 120;
        supplyOne.setName(newName);
        supplyOne.setPrice(newPrice);
        supplyOne.setDuration(newDuration);
        supplyOne.setDtUpdate(LocalDateTime.now());
        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.update(supplyOne));
    }


    @Test
    void shouldDeleteProperly() {
        supplyOne = this.supplyDao.save(supplyOne);
        User updatedMaster = this.userDao.get(master.getUuid()).orElseThrow();
        supplyOne.setMasters(List.of(updatedMaster));

        this.supplyDao.delete(supplyOne);

        Optional<Supply> optionalSupply = this.supplyDao.get(supplyOne.getUuid());
        assertThrows(NoSuchElementException.class, optionalSupply::orElseThrow);
    }

    @Test
    void shouldThrowWhileDeleteWrongUuid() {
        this.supplyDao.save(supplyOne);

        supplyOne.setUuid(UUID.randomUUID());

        assertThrows(DeletingDBDataException.class, () -> this.supplyDao.delete(supplyOne));
    }

    @Test
    void shouldThrowWhileDeleteNotUpToDatedObject() {
        this.supplyDao.save(supplyOne);

        supplyOne.setDtUpdate(LocalDateTime.now());

        assertThrows(DeletingDBDataException.class, () -> this.supplyDao.delete(supplyOne));
    }


}