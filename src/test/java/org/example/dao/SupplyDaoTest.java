package org.example.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.core.entity.Supply;
import org.example.core.exceptions.NullArgumentException;
import org.example.dao.api.ISupplyDao;
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

    ISupplyDao supplyDao;

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
        try (Connection connection = dataBaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE app.supply RESTART IDENTITY CASCADE");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка очищения базы данных", e);
        }
        supplyOne = new Supply(UUID.randomUUID(), "cut", new BigDecimal("50.50"), 100, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
        supplyTwo = new Supply(UUID.randomUUID(), "non-cut", new BigDecimal("150.50"), 150, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
    }


    @Test
    void shouldGetProperly() {
        supplyDao.save(supplyOne);
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
    void shouldGetByListUuidProperly() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        List<UUID> supplyUuids = List.of(supplyOne.getUuid(), supplyTwo.getUuid());

        List<Supply> supplies = this.supplyDao.get(supplyUuids);
        assertEquals(2, supplies.size());
    }

    @Test
    void shouldGetByEmptyListUuidProperly() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        List<UUID> supplyUuids = new ArrayList<>();

        List<Supply> supplies = this.supplyDao.get(supplyUuids);
        assertEquals(0, supplies.size());
    }

    @Test
    void shouldGetByUuidProperly() {
        supplyDao.save(supplyOne);
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
    void shouldGetByUuidWithoutMasterProperly() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        Supply supply = this.supplyDao.getWithoutMasters(supplyOne.getUuid()).orElseThrow();

        assertEquals(supplyOne, supply);
    }

    @Test
    void shouldThrowWhileGetByWrongUuidWithoutMaster() {
        supplyDao.save(supplyOne);
        Optional<Supply> supplyOptional = this.supplyDao.getWithoutMasters(UUID.randomUUID());

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
    void shouldSystemUpdateProperly() {
        this.supplyDao.save(supplyOne);
        assertDoesNotThrow(() -> this.supplyDao.systemUpdate(supplyOne));
    }

    @Test
    void shouldThrowWhileSystemUpdateNotExistedSupply() {
        this.supplyDao.save(supplyOne);
        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.systemUpdate(supplyTwo));
    }

    @Test
    void shouldExists() {
        this.supplyDao.save(supplyOne);
        assertTrue(this.supplyDao.exists(supplyOne.getUuid()));
    }

    @Test
    void shouldNotExists() {
        this.supplyDao.save(supplyOne);
        assertFalse(this.supplyDao.exists(supplyTwo.getUuid()));
    }

    @Test
    void shouldUpdateProperly() {
        this.supplyDao.save(supplyOne);

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
        this.supplyDao.save(supplyOne);

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
        this.supplyDao.save(supplyOne);

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