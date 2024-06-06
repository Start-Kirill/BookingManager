//package org.example.dao;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import org.example.core.entity.Supply;
//import org.example.core.entity.User;
//import org.example.core.enums.UserRole;
//import org.example.core.exceptions.NullArgumentException;
//import org.example.dao.api.ISupplyDao;
//import org.example.dao.api.IUserDao;
//import org.example.dao.ds.DataBaseConnection;
//import org.example.dao.exceptions.CreatingDBDataException;
//import org.example.dao.exceptions.DeletingDBDataException;
//import org.example.dao.exceptions.UpdatingDBDataException;
//import org.junit.jupiter.api.*;
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
//class UserDaoTest {
//
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
//            "postgres:15-alpine"
//    );
//
//    IUserDao userDao;
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
//        userDao = new UserDao(supplyDao, dataBaseConnection);
//        try (Connection connection = dataBaseConnection.getConnection();
//             Statement statement = connection.createStatement()) {
//            statement.execute("TRUNCATE TABLE app.supply RESTART IDENTITY CASCADE");
//            statement.execute("TRUNCATE TABLE app.users RESTART IDENTITY CASCADE");
//        } catch (Exception e) {
//            throw new RuntimeException("Ошибка очищения базы данных", e);
//        }
//    }
//
//    @Test
//    void shouldGetProperly() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//
//        this.userDao.save(user);
//
//        List<User> users = this.userDao.get();
//        Assertions.assertEquals(1, users.size());
//        Assertions.assertEquals(user, users.get(0));
//    }
//
//    @Test
//    void shouldGetEmpty() {
//        List<User> users = this.userDao.get();
//
//        assertEquals(0, users.size());
//        assertEquals(List.of(), users);
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
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//
//        this.userDao.save(user);
//
//        User actualUser = this.userDao.get(userUuid).orElseThrow();
//        Assertions.assertEquals(user, actualUser);
//    }
//
//    @Test
//    void shouldThrowWhileGetByWrongUuid() {
//        UUID uuidOne = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        supplyDao.save(supplyOne);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne), now, now);
//
//        this.userDao.save(user);
//
//        Optional<User> userOptional = this.userDao.get(UUID.randomUUID());
//
//        assertThrows(NoSuchElementException.class, userOptional::orElseThrow);
//    }
//
//    @Test
//    void shouldThrowWhileGetByNullUuid() {
//        UUID uuidOne = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        supplyDao.save(supplyOne);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne), now, now);
//
//        this.userDao.save(user);
//
//        UUID nullUuid = null;
//        assertThrows(NullArgumentException.class, () -> this.userDao.get(nullUuid));
//    }
//
//    @Test
//    void shouldSaveProperly() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//
//        User saved = this.userDao.save(user);
//
//        assertEquals(user, saved);
//    }
//
//    @Test
//    void shouldSaveWithNullPhoneNumber() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//
//        User saved = this.userDao.save(user);
//
//        assertEquals(user, saved);
//    }
//
//    @Test
//    void shouldThrowWhileSaveNull() {
//        assertThrows(NullArgumentException.class, () -> this.userDao.save(null));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullUuidField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = null;
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullNameField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, null, "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullUserRoleField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", null, List.of(supplyOne, supplyTwo), now, now);
//        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullDtCreateField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), null, now);
//        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNullDtUpdateField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), now, null);
//        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithInvalidPhoneNumber() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//        assertThrows(CreatingDBDataException.class, () -> this.userDao.save(user));
//    }
//
//    @Test
//    void shouldUpdateProperly() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", "+3751234578", UserRole.MASTER, List.of(supplyOne), now, now);
//        this.userDao.save(user);
//
//        String newName = "Sasha";
//        String newPhoneNumber = "+123456789";
//        UserRole newUserRole = UserRole.ADMIN;
//        List<Supply> newSupplies = List.of(supplyTwo);
//        User updated = this.userDao.update(new User(userUuid, newName, newPhoneNumber, newUserRole, newSupplies, now, now));
//
//        assertEquals(newName, updated.getName());
//        assertEquals(newPhoneNumber, updated.getPhoneNumber());
//        assertEquals(newUserRole, updated.getUserRole());
//        assertEquals(newSupplies, updated.getSupplies());
//        assertEquals(now, updated.getDtCreate());
//        assertNotEquals(now, updated.getDtUpdate());
//    }
//
//    @Test
//    void shouldUpdateWithNullPhoneNumber() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//        this.userDao.save(user);
//
//        String newName = "Sasha";
//        String newPhoneNumber = null;
//        UserRole newUserRole = UserRole.ADMIN;
//        List<Supply> newSupplies = List.of(supplyTwo);
//        User updated = this.userDao.update(new User(userUuid, newName, newPhoneNumber, newUserRole, newSupplies, now, now));
//
//        assertEquals(newName, updated.getName());
//        assertEquals(newPhoneNumber, updated.getPhoneNumber());
//        assertEquals(newUserRole, updated.getUserRole());
//        assertEquals(newSupplies, updated.getSupplies());
//        assertEquals(now, updated.getDtCreate());
//        assertNotEquals(now, updated.getDtUpdate());
//    }
//
//    @Test
//    void shouldThrowWhilUpdateNull() {
//        assertThrows(NullArgumentException.class, () -> this.userDao.update(null));
//    }
//
//    @Test
//    void shouldThrowWhileUpdateWithNullUuidField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//        this.userDao.save(user);
//
//        String newName = "Sasha";
//        String newPhoneNumber = "+12346798";
//        UserRole newUserRole = UserRole.ADMIN;
//        List<Supply> newSupplies = List.of(supplyTwo);
//        User newUser = new User(null, newName, newPhoneNumber, newUserRole, newSupplies, now, now);
//        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(newUser));
//
//    }
//
//    @Test
//    void shouldThrowWhileUpdateWithNullNameField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//        this.userDao.save(user);
//
//        String newName = null;
//        String newPhoneNumber = "+12346798";
//        UserRole newUserRole = UserRole.ADMIN;
//        List<Supply> newSupplies = List.of(supplyTwo);
//        User newUser = new User(userUuid, newName, newPhoneNumber, newUserRole, newSupplies, now, now);
//        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(newUser));
//    }
//
//    @Test
//    void shouldThrowWhileUpdateWithNullUserRoleField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//        this.userDao.save(user);
//
//        String newName = "Kirill";
//        String newPhoneNumber = "+12346798";
//        UserRole newUserRole = null;
//        List<Supply> newSupplies = List.of(supplyTwo);
//        User newUser = new User(userUuid, newName, newPhoneNumber, newUserRole, newSupplies, now, now);
//        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(newUser));
//    }
//
//
//    @Test
//    void shouldThrowWhileUpdateWithNullDtUpdateField() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne, supplyTwo), now, now);
//        this.userDao.save(user);
//
//        String newName = "Kirill";
//        String newPhoneNumber = "+12346798";
//        UserRole newUserRole = UserRole.ADMIN;
//        List<Supply> newSupplies = List.of(supplyTwo);
//        User newUser = new User(userUuid, newName, newPhoneNumber, newUserRole, newSupplies, now, null);
//        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(newUser));
//    }
//
//    @Test
//    void shouldThrowWhileSaveWithNotExistedSupply() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne), now, now);
//        this.userDao.save(user);
//
//        String newName = "Kirill";
//        String newPhoneNumber = "+12346798";
//        UserRole newUserRole = UserRole.ADMIN;
//        List<Supply> newSupplies = List.of(supplyTwo);
//        User newUser = new User(userUuid, newName, newPhoneNumber, newUserRole, newSupplies, now, LocalDateTime.now());
//        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(newUser));
//    }
//
//    @Test
//    void shouldThrowWhileUpdateNotUpToDatedObject() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne), now, now);
//        this.userDao.save(user);
//
//        String newName = "Kirill";
//        String newPhoneNumber = "+12346798";
//        UserRole newUserRole = UserRole.ADMIN;
//        List<Supply> newSupplies = List.of(supplyTwo);
//        User newUser = new User(userUuid, newName, newPhoneNumber, newUserRole, newSupplies, now, now);
//        assertThrows(UpdatingDBDataException.class, () -> this.userDao.update(newUser));
//    }
//
//    @Test
//    void shouldDeleteProperly() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne), now, now);
//        this.userDao.save(user);
//
//        this.userDao.delete(user);
//
//        Optional<User> userOptional = this.userDao.get(userUuid);
//        assertThrows(NoSuchElementException.class, userOptional::orElseThrow);
//    }
//
//    @Test
//    void shouldThrowWhileDeleteWrongUuid() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne), now, now);
//        this.userDao.save(user);
//
//        user.setUuid(UUID.randomUUID());
//
//        assertThrows(DeletingDBDataException.class, () -> this.userDao.delete(user));
//    }
//
//    @Test
//    void shouldThrowWhileDeleteNotUpToDatedObject() {
//        UUID uuidOne = UUID.randomUUID();
//        UUID uuidTwo = UUID.randomUUID();
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyOne = new Supply(uuidOne, "cut", new BigDecimal("50.50"), 100, now, now);
//        now = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
//        Supply supplyTwo = new Supply(uuidTwo, "non-cut", new BigDecimal("150.50"), 150, now, now);
//
//        supplyDao.save(supplyOne);
//        supplyDao.save(supplyTwo);
//
//        UUID userUuid = UUID.randomUUID();
//        User user = new User(userUuid, "Kirill", null, UserRole.MASTER, List.of(supplyOne), now, now);
//        this.userDao.save(user);
//
//        user.setDtUpdate(LocalDateTime.now());
//
//        assertThrows(DeletingDBDataException.class, () -> this.userDao.delete(user));
//    }
//}