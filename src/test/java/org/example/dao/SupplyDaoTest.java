//package org.example.dao;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import org.example.core.entity.Supply;
//import org.example.core.exceptions.NullArgumentException;
//import org.example.dao.api.ISupplyDao;
//import org.example.dao.ds.DataBaseConnection;
//import org.example.dao.exceptions.CreatingDBDataException;
//import org.example.dao.exceptions.DeletingDBDataException;
//import org.example.dao.exceptions.UpdatingDBDataException;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.testcontainers.containers.PostgreSQLContainer;
//
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.sql.Statement;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//class SupplyDaoTest {
//
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
//            "postgres:15-alpine"
//    );
//
//
//    ISupplyDao supplyDao;
//
//    @BeforeAll
//    static void beforeAll() {
//        postgres.withInitScript("ddl/0_init.sql");
//        postgres.start();
//    }
//
//    @AfterAll
//    static void afterAll() {
//        postgres.stop();
//    }
//
//    @BeforeEach
//    void setUp() {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl(postgres.getJdbcUrl());
//        config.setUsername(postgres.getUsername());
//        config.setPassword(postgres.getPassword());
//        config.setMaximumPoolSize(2);
//        DataBaseConnection dataBaseConnection = new DataBaseConnection(new HikariDataSource(config));
////        supplyDao = new SupplyDao(dataBaseConnection);
//        try (Connection connection = dataBaseConnection.getConnection();
//             Statement statement = connection.createStatement()) {
//            statement.execute("TRUNCATE TABLE app.supply RESTART IDENTITY CASCADE");
//        } catch (Exception e) {
//            throw new RuntimeException("Ошибка очищения базы данных", e);
//        }
//    }
//
//
//    @Test
//    void shouldGetProperly() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        List<Supply> supplies = this.supplyDao.get();
//
//        assertEquals(2, supplies.size());
//        assertEquals(List.of(supplyOne, supplyTwo), supplies);
//    }
//
//    @Test
//    void shouldGetEmpty() {
//        List<Supply> supplies = this.supplyDao.get();
//
//        assertEquals(0, supplies.size());
//        assertEquals(List.of(), supplies);
//    }
//
//    @Test
//    void shouldGetByUuidProperly() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        Supply supply = this.supplyDao.get(uuidTwo).orElseThrow();
//
//        assertEquals(supplyTwo, supply);
//    }
//
//    @Test
//    void shouldThrowWhileGetByWrongUuid() {
//        UUID uuidOne = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        supplyDao.save(supplyOne);
//        Optional<Supply> supplyOptional = this.supplyDao.get(UUID.randomUUID());
//
//        assertThrows(NoSuchElementException.class, supplyOptional::orElseThrow);
//    }
//
//    @Test
//    void shouldThrowWhileGetByNullUuid() {
//        UUID uuidOne = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        supplyDao.save(supplyOne);
//
//        UUID nullUuid = null;
//        assertThrows(NullArgumentException.class, () -> this.supplyDao.get(nullUuid));
//    }
//
//    @Test
//    void shouldSaveProperly() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), null, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        Supply savedOne = supplyDao.save(supplyOne);
//        Supply savedTwo = supplyDao.save(supplyTwo);
//
//        assertEquals(supplyOne, savedOne);
//        assertEquals(supplyTwo, savedTwo);
//    }
//
//    @Test
//    void shouldThrowWhileSaveNull() {
//        assertThrows(NullArgumentException.class, () -> supplyDao.save(null));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullUuidField() {
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(null, "cut", new BigDecimal("50.50"), 100, now, now);
//        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supply));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullPriceField() {
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(UUID.randomUUID(), "cut", null, 100, now, now);
//        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supply));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullNameField() {
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(UUID.randomUUID(), null, new BigDecimal("50.50"), 100, now, now);
//        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supply));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullDtCreateField() {
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(UUID.randomUUID(), "cut", new BigDecimal("50.50"), 100, null, now);
//        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supply));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullDtUpdateField() {
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(UUID.randomUUID(), "cut", new BigDecimal("50.50"), 100, now, null);
//        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supply));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithExistedName() {
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(UUID.randomUUID(), "cut", new BigDecimal("50.50"), 100, now, now);
//        Supply supplyWithSameName = new Supply(UUID.randomUUID(), "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//        assertThrows(CreatingDBDataException.class, () -> supplyDao.save(supplyWithSameName));
//    }
//
//    @Test
//    void shouldUpdateProperly() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        String newName = "staining";
//        BigDecimal newPrice = BigDecimal.valueOf(100.70);
//        Integer newDuration = 120;
//        Supply updated = this.supplyDao.update(new Supply(uuid, newName, newPrice, newDuration, now, now));
//
//        assertEquals(newName, updated.getName());
//        assertEquals(newPrice, updated.getPrice());
//        assertEquals(newDuration, updated.getDuration());
//        assertEquals(now, updated.getDtCreate());
//        assertNotEquals(now, updated.getDtUpdate());
//    }
//
//    @Test
//    void shouldUpdateNullDurationProperly() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        String newName = "staining";
//        BigDecimal newPrice = BigDecimal.valueOf(100.70);
//        Integer newDuration = null;
//        Supply updated = this.supplyDao.update(new Supply(uuid, newName, newPrice, newDuration, now, now));
//
//        assertEquals(newName, updated.getName());
//        assertEquals(newPrice, updated.getPrice());
//        assertNull(updated.getDuration());
//        assertEquals(now, updated.getDtCreate());
//        assertNotEquals(now, updated.getDtUpdate());
//    }
//
//    @Test
//    void shouldThrowWhileUpdateNull() {
//        assertThrows(NullArgumentException.class, () -> this.supplyDao.update(null));
//    }
//
//    @Test
//    void shouldThrowWhileUpdateWithNullUuidField() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        String newName = "staining";
//        BigDecimal newPrice = BigDecimal.valueOf(100.70);
//        Integer newDuration = 120;
//        Supply newSupply = new Supply(null, newName, newPrice, newDuration, now, now);
//        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.update(newSupply));
//    }
//
//    @Test
//    void shouldThrowWhileUpdateWithNullNameField() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        String newName = null;
//        BigDecimal newPrice = BigDecimal.valueOf(100.70);
//        Integer newDuration = 120;
//        Supply newSupply = new Supply(uuid, newName, newPrice, newDuration, now, now);
//        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.update(newSupply));
//    }
//
//    @Test
//    void shouldThrowWhileUpdateWithNullPriceField() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        String newName = "staining";
//        BigDecimal newPrice = null;
//        Integer newDuration = 120;
//        Supply newSupply = new Supply(uuid, newName, newPrice, newDuration, now, now);
//        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.update(newSupply));
//    }
//
//    @Test
//    void shouldThrowWhileUpdateNotUpToDatedObject() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        String newName = "staining";
//        BigDecimal newPrice = BigDecimal.valueOf(100.70);
//        Integer newDuration = 120;
//        Supply newSupply = new Supply(uuid, newName, newPrice, newDuration, now, LocalDateTime.now());
//        assertThrows(UpdatingDBDataException.class, () -> this.supplyDao.update(newSupply));
//    }
//
//
//    @Test
//    void shouldDeleteProperly() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        this.supplyDao.delete(supply);
//
//        Optional<Supply> optionalSupply = this.supplyDao.get(uuid);
//        assertThrows(NoSuchElementException.class, optionalSupply::orElseThrow);
//    }
//
//    @Test
//    void shouldThrowWhileDeleteWrongUuid() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        supply.setUuid(UUID.randomUUID());
//
//        assertThrows(DeletingDBDataException.class, () -> this.supplyDao.delete(supply));
//    }
//
//    @Test
//    void shouldThrowWhileDeleteNotUpToDatedObject() {
//        UUID uuid = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supply = new Supply(uuid, "cut", new BigDecimal("50.50"), 100, now, now);
//        this.supplyDao.save(supply);
//
//        String newName = "staining";
//        BigDecimal newPrice = BigDecimal.valueOf(100.70);
//        Integer newDuration = 120;
//        Supply newSupply = new Supply(uuid, newName, newPrice, newDuration, now, LocalDateTime.now());
//        assertThrows(DeletingDBDataException.class, () -> this.supplyDao.delete(newSupply));
//    }
//
//
//}