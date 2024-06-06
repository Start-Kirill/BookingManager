package org.example.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.core.exceptions.NullArgumentException;
import org.example.dao.api.ISupplyDao;
import org.example.dao.api.IUserDao;
import org.example.dao.ds.DataBaseConnection;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    Supply supplyOne;

    Supply supplyTwo;

    User user;

    IUserDao userDao;

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
        userDao = new UserDao(supplyDao, dataBaseConnection);
        try (Connection connection = dataBaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE app.supply RESTART IDENTITY CASCADE");
            statement.execute("TRUNCATE TABLE app.users RESTART IDENTITY CASCADE");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка очищения базы данных", e);
        }
        LocalDateTime now = LocalDateTime.now();
        supplyOne = new Supply(UUID.randomUUID(), "cut", new BigDecimal("50.50"), 100, new ArrayList<>(), now, now);
        supplyTwo = new Supply(UUID.randomUUID(), "non-cut", new BigDecimal("150.50"), 150, new ArrayList<>(), now, now);
        user = new User(UUID.randomUUID(), "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
    }

    @Test
    void shouldGetProperly() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        List<User> users = this.userDao.get();
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals(user, users.get(0));
    }

    @Test
    void shouldGetEmpty() {
        List<User> users = this.userDao.get();

        assertEquals(0, users.size());
        assertEquals(List.of(), users);
    }

    @Test
    void shouldGetByUuidProperly() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        User saved = this.userDao.save(user);

        User actualUser = this.userDao.get(user.getUuid()).orElseThrow();
        Assertions.assertEquals(saved, actualUser);
    }

    @Test
    void shouldThrowWhileGetByWrongUuid() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        Optional<User> userOptional = this.userDao.get(UUID.randomUUID());

        assertThrows(NoSuchElementException.class, userOptional::orElseThrow);
    }

    @Test
    void shouldThrowWhileGetByNullUuid() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        UUID nullUuid = null;
        assertThrows(NullArgumentException.class, () -> this.userDao.get(nullUuid));
    }

    @Test
    void shouldSaveProperly() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        User saved = this.userDao.save(user);

        assertEquals(user, saved);
    }

    @Test
    void shouldSaveWithNullPhoneNumber() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        user.setPhoneNumber(null);

        User saved = this.userDao.save(user);

        assertEquals(user, saved);
    }

    @Test
    void shouldThrowWhileSaveNull() {
        assertThrows(NullArgumentException.class, () -> this.userDao.save(null));
    }

    @Test
    void shouldThrowWhileSaveWithNullUuidField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        user.setUuid(null);

        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
    }

    @Test
    void shouldThrowWhileSaveWithNullNameField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        user.setName(null);
        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
    }

    @Test
    void shouldThrowWhileSaveWithNullUserRoleField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        user.setUserRole(null);

        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
    }

    @Test
    void shouldThrowWhileSaveWithNullDtCreateField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        user.setDtCreate(null);

        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
    }

    @Test
    void shouldThrowWhileSaveWithNullDtUpdateField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        user.setDtUpdate(null);
        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
    }

    @Test
    void shouldThrowWhileSaveWithNotExistedSupply() {
        supplyDao.save(supplyOne);
        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
    }

    @Test
    void shouldUpdateWithNullPhoneNumber() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        String newName = "Sasha";
        String newPhoneNumber = null;
        UserRole newUserRole = UserRole.ADMIN;
        List<Supply> newSupplies = List.of(this.supplyDao.get(supplyTwo.getUuid()).orElseThrow());
        user.setName(newName);
        user.setPhoneNumber(newPhoneNumber);
        user.setUserRole(newUserRole);
        user.setSupplies(newSupplies);

        User updated = this.userDao.update(user);

        assertEquals(newName, updated.getName());
        assertEquals(newPhoneNumber, updated.getPhoneNumber());
        assertEquals(newUserRole, updated.getUserRole());
        assertEquals(newSupplies, updated.getSupplies());
        assertEquals(user.getDtUpdate().truncatedTo(ChronoUnit.MILLIS), updated.getDtCreate().truncatedTo(ChronoUnit.MILLIS));
        assertNotEquals(user.getDtCreate().truncatedTo(ChronoUnit.MILLIS), updated.getDtUpdate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void shouldUpdateProperly() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        String newName = "Sasha";
        String newPhoneNumber = "+123456789";
        UserRole newUserRole = UserRole.ADMIN;
        List<Supply> newSupplies = List.of(this.supplyDao.get(supplyTwo.getUuid()).orElseThrow());
        user.setName(newName);
        user.setPhoneNumber(newPhoneNumber);
        user.setUserRole(newUserRole);
        user.setSupplies(newSupplies);

        User updated = this.userDao.update(user);

        assertEquals(newName, updated.getName());
        assertEquals(newPhoneNumber, updated.getPhoneNumber());
        assertEquals(newUserRole, updated.getUserRole());
        assertEquals(newSupplies, updated.getSupplies());
        assertEquals(user.getDtUpdate().truncatedTo(ChronoUnit.MILLIS), updated.getDtCreate().truncatedTo(ChronoUnit.MILLIS));
        assertNotEquals(user.getDtCreate().truncatedTo(ChronoUnit.MILLIS), updated.getDtUpdate().truncatedTo(ChronoUnit.MILLIS));
    }


    @Test
    void shouldThrowWhileUpdateNull() {
        assertThrows(NullArgumentException.class, () -> this.userDao.update(null));
    }

    @Test
    void shouldThrowWhileUpdateWithNullUuidField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        String newName = "Sasha";
        String newPhoneNumber = "+12346798";
        UserRole newUserRole = UserRole.ADMIN;
        List<Supply> newSupplies = List.of(supplyTwo);
        user.setUuid(null);
        user.setName(newName);
        user.setPhoneNumber(newPhoneNumber);
        user.setUserRole(newUserRole);
        user.setSupplies(newSupplies);

        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(user));

    }

    @Test
    void shouldThrowWhileUpdateWithNullNameField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        String newName = null;
        String newPhoneNumber = "+12346798";
        UserRole newUserRole = UserRole.ADMIN;
        List<Supply> newSupplies = List.of(supplyTwo);
        user.setName(newName);
        user.setPhoneNumber(newPhoneNumber);
        user.setUserRole(newUserRole);
        user.setSupplies(newSupplies);

        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(user));
    }

    @Test
    void shouldThrowWhileUpdateWithNullUserRoleField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        String newName = "Sasha";
        String newPhoneNumber = "+12346798";
        UserRole newUserRole = null;
        List<Supply> newSupplies = List.of(supplyTwo);
        user.setName(newName);
        user.setPhoneNumber(newPhoneNumber);
        user.setUserRole(newUserRole);
        user.setSupplies(newSupplies);

        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(user));
    }


    @Test
    void shouldThrowWhileUpdateWithNullDtUpdateField() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        String newName = "Sasha";
        String newPhoneNumber = "+12346798";
        UserRole newUserRole = UserRole.ADMIN;
        List<Supply> newSupplies = List.of(supplyTwo);
        user.setName(newName);
        user.setPhoneNumber(newPhoneNumber);
        user.setUserRole(newUserRole);
        user.setSupplies(newSupplies);
        user.setDtUpdate(null);

        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(user));
    }

    @Test
    void shouldThrowWhileUpdateNotUpToDatedObject() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        String newName = "Sasha";
        String newPhoneNumber = "+12346798";
        UserRole newUserRole = UserRole.ADMIN;
        List<Supply> newSupplies = List.of(supplyTwo);
        user.setName(newName);
        user.setPhoneNumber(newPhoneNumber);
        user.setUserRole(newUserRole);
        user.setSupplies(newSupplies);
        user.setDtUpdate(LocalDateTime.now());

        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(user));
    }

    @Test
    void shouldDeleteProperly() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        User saved = this.userDao.save(user);

        this.userDao.delete(saved);

        Optional<User> userOptional = this.userDao.get(user.getUuid());
        assertThrows(NoSuchElementException.class, userOptional::orElseThrow);
    }

    @Test
    void shouldThrowWhileDeleteWrongUuid() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        user.setUuid(UUID.randomUUID());

        assertThrows(DeletingDBDataException.class, () -> this.userDao.delete(user));
    }

    @Test
    void shouldThrowWhileDeleteNotUpToDatedObject() {
        supplyDao.save(supplyOne);
        supplyDao.save(supplyTwo);

        this.userDao.save(user);

        user.setDtUpdate(LocalDateTime.now());

        assertThrows(DeletingDBDataException.class, () -> this.userDao.delete(user));
    }
}