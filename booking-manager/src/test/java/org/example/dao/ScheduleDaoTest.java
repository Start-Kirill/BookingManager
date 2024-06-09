package org.example.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.core.entity.Schedule;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.core.exceptions.NullArgumentException;
import org.example.dao.ds.DataBaseConnection;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleDaoTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    UserDao userDao;

    ScheduleDao scheduleDao;

    User masterOne = new User(UUID.randomUUID(), "Kirill", "+13456789", UserRole.MASTER, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());

    User masterTwo = new User(UUID.randomUUID(), "Volodya", "+13456789", UserRole.MASTER, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
    Schedule scheduleOne;

    Schedule scheduleTwo;

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
        userDao = new UserDao(dataBaseConnection);
        scheduleDao = new ScheduleDao(userDao, dataBaseConnection);
        try (Connection connection = dataBaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE app.users RESTART IDENTITY CASCADE");
            statement.execute("TRUNCATE TABLE app.schedule RESTART IDENTITY CASCADE");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка очищения базы данных", e);
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dtStart = now.plusHours(1);
        LocalDateTime dtEnd = dtStart.plusHours(8);
        scheduleOne = new Schedule(UUID.randomUUID(), masterOne, dtStart, dtEnd, now, now);
        scheduleTwo = new Schedule(UUID.randomUUID(), masterOne, dtStart.plusDays(1), dtEnd.plusDays(1), now, now);
        this.userDao.save(masterOne);
        this.userDao.save(masterTwo);
    }

    @Test
    void shouldGetByUuidProperly() {
        this.scheduleDao.save(scheduleOne);
        Schedule schedule = this.scheduleDao.get(scheduleOne.getUuid()).orElseThrow();
        assertEquals(scheduleOne, schedule);
    }

    @Test
    void shouldThrowWhileGetByWrongUuid() {
        Optional<Schedule> optionalSchedule = this.scheduleDao.get(UUID.randomUUID());
        Assertions.assertThrows(NoSuchElementException.class, optionalSchedule::orElseThrow);
    }

    @Test
    void shouldThrowWhileGetByNullUuid() {
        Assertions.assertThrows(NullArgumentException.class, () -> this.scheduleDao.get(null));
    }

    @Test
    void shouldGetProperly() {
        this.scheduleDao.save(scheduleOne);
        this.scheduleDao.save(scheduleTwo);

        List<Schedule> schedules = this.scheduleDao.get();

        assertEquals(2, schedules.size());
        assertEquals(List.of(scheduleOne, scheduleTwo), schedules);
    }

    @Test
    void shouldGetEmpty() {
        List<Schedule> schedules = this.scheduleDao.get();

        assertEquals(0, schedules.size());
        assertEquals(List.of(), schedules);
    }


    @Test
    void shouldSaveProperly() {
        Schedule schedule = this.scheduleDao.save(scheduleOne);
        Schedule gottenSchedule = this.scheduleDao.get(scheduleOne.getUuid()).orElseThrow();

        assertEquals(scheduleOne, schedule);
        assertEquals(scheduleOne, gottenSchedule);
    }

    @Test
    void shouldSaveProperlyWithNullDtStartField() {
        scheduleOne.setDtStart(null);
        Schedule schedule = this.scheduleDao.save(scheduleOne);
        Schedule gottenSchedule = this.scheduleDao.get(scheduleOne.getUuid()).orElseThrow();

        assertEquals(scheduleOne, schedule);
        assertEquals(scheduleOne, gottenSchedule);
    }

    @Test
    void shouldSaveProperlyWithNullDtEndField() {
        scheduleOne.setDtEnd(null);
        Schedule schedule = this.scheduleDao.save(scheduleOne);
        Schedule gottenSchedule = this.scheduleDao.get(scheduleOne.getUuid()).orElseThrow();

        assertEquals(scheduleOne, schedule);
        assertEquals(scheduleOne, gottenSchedule);
    }

    @Test
    void shouldThrowWhileSaveNull() {
        assertThrows(NullArgumentException.class, () -> this.scheduleDao.save(null));
    }

    @Test
    void shouldThrowWhileSaveWithNullUuidFiled() {
        scheduleOne.setUuid(null);
        assertThrows(CreatingDBDataException.class, () -> this.scheduleDao.save(scheduleOne));
    }

    @Test
    void shouldThrowWhileSaveWithNullMasterFiled() {
        scheduleOne.setMaster(null);
        assertThrows(CreatingDBDataException.class, () -> this.scheduleDao.save(scheduleOne));
    }

    @Test
    void shouldThrowWhileSaveWitNullDtCreateFiled() {
        scheduleOne.setDtCreate(null);
        assertThrows(CreatingDBDataException.class, () -> this.scheduleDao.save(scheduleOne));
    }

    @Test
    void shouldThrowWhileSaveWithNullDtUpdateFiled() {
        scheduleOne.setDtUpdate(null);
        assertThrows(CreatingDBDataException.class, () -> this.scheduleDao.save(scheduleOne));
    }

    @Test
    void shouldUpdateProperly() {
        this.scheduleDao.save(scheduleOne);

        LocalDateTime newDtStart = LocalDateTime.now().plusHours(2);
        LocalDateTime newDtEnd = newDtStart.plusHours(9);

        scheduleOne.setMaster(masterTwo);
        scheduleOne.setDtStart(newDtStart);
        scheduleOne.setDtEnd(newDtEnd);

        Schedule update = this.scheduleDao.update(scheduleOne);

        assertEquals(scheduleOne.getUuid(), update.getUuid());
        assertEquals(scheduleOne.getMaster(), update.getMaster());
        assertEquals(scheduleOne.getDtStart().truncatedTo(ChronoUnit.MILLIS), update.getDtStart().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(scheduleOne.getDtEnd().truncatedTo(ChronoUnit.MILLIS), update.getDtEnd().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(scheduleOne.getDtCreate().truncatedTo(ChronoUnit.MILLIS), update.getDtCreate().truncatedTo(ChronoUnit.MILLIS));
        assertNotEquals(scheduleOne.getDtUpdate().truncatedTo(ChronoUnit.MILLIS), update.getDtUpdate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void shouldUpdateNullDtStartProperly() {
        this.scheduleDao.save(scheduleOne);

        LocalDateTime newDtStart = LocalDateTime.now().plusHours(2);
        LocalDateTime newDtEnd = newDtStart.plusHours(9);

        scheduleOne.setMaster(masterTwo);
        scheduleOne.setDtStart(null);
        scheduleOne.setDtEnd(newDtEnd);

        Schedule update = this.scheduleDao.update(scheduleOne);

        assertEquals(scheduleOne.getUuid(), update.getUuid());
        assertEquals(scheduleOne.getMaster(), update.getMaster());
        assertEquals(scheduleOne.getDtStart(), update.getDtStart());
        assertEquals(scheduleOne.getDtEnd().truncatedTo(ChronoUnit.MILLIS), update.getDtEnd().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(scheduleOne.getDtCreate().truncatedTo(ChronoUnit.MILLIS), update.getDtCreate().truncatedTo(ChronoUnit.MILLIS));
        assertNotEquals(scheduleOne.getDtUpdate().truncatedTo(ChronoUnit.MILLIS), update.getDtUpdate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void shouldUpdateNullDtEndProperly() {
        this.scheduleDao.save(scheduleOne);

        LocalDateTime newDtStart = LocalDateTime.now().plusHours(2);
        LocalDateTime newDtEnd = newDtStart.plusHours(9);

        scheduleOne.setMaster(masterTwo);
        scheduleOne.setDtStart(newDtStart);
        scheduleOne.setDtEnd(null);

        Schedule update = this.scheduleDao.update(scheduleOne);

        assertEquals(scheduleOne.getUuid(), update.getUuid());
        assertEquals(scheduleOne.getMaster(), update.getMaster());
        assertEquals(scheduleOne.getDtStart().truncatedTo(ChronoUnit.MILLIS), update.getDtStart().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(scheduleOne.getDtEnd(), update.getDtEnd());
        assertEquals(scheduleOne.getDtCreate().truncatedTo(ChronoUnit.MILLIS), update.getDtCreate().truncatedTo(ChronoUnit.MILLIS));
        assertNotEquals(scheduleOne.getDtUpdate().truncatedTo(ChronoUnit.MILLIS), update.getDtUpdate().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void shouldThrowWhileUpdateNull() {
        assertThrows(NullArgumentException.class, () -> this.scheduleDao.update(null));
    }

    @Test
    void shouldThrowWhileUpdateWithNullUuidField() {
        this.scheduleDao.save(scheduleOne);

        LocalDateTime newDtStart = LocalDateTime.now().plusHours(2);
        LocalDateTime newDtEnd = newDtStart.plusHours(9);

        scheduleOne.setUuid(null);
        scheduleOne.setMaster(masterTwo);
        scheduleOne.setDtStart(newDtStart);
        scheduleOne.setDtEnd(newDtEnd);

        assertThrows(UpdatingDBDataException.class, () -> this.scheduleDao.update(scheduleOne));
    }

    @Test
    void shouldThrowWhileUpdateWithNullMasterField() {
        this.scheduleDao.save(scheduleOne);

        LocalDateTime newDtStart = LocalDateTime.now().plusHours(2);
        LocalDateTime newDtEnd = newDtStart.plusHours(9);

        scheduleOne.setMaster(null);
        scheduleOne.setDtStart(newDtStart);
        scheduleOne.setDtEnd(newDtEnd);

        assertThrows(UpdatingDBDataException.class, () -> this.scheduleDao.update(scheduleOne));
    }

    @Test
    void shouldThrowWhileUpdateWithNullDtUpdateField() {
        this.scheduleDao.save(scheduleOne);

        LocalDateTime newDtStart = LocalDateTime.now().plusHours(2);
        LocalDateTime newDtEnd = newDtStart.plusHours(9);

        scheduleOne.setDtUpdate(null);
        scheduleOne.setMaster(masterTwo);
        scheduleOne.setDtStart(newDtStart);
        scheduleOne.setDtEnd(newDtEnd);

        assertThrows(UpdatingDBDataException.class, () -> this.scheduleDao.update(scheduleOne));
    }

    @Test
    void shouldThrowWhileUpdateNotUpToDatedObject() {
        this.scheduleDao.save(scheduleOne);

        LocalDateTime newDtStart = LocalDateTime.now().plusHours(2);
        LocalDateTime newDtEnd = newDtStart.plusHours(9);

        scheduleOne.setDtUpdate(LocalDateTime.now());
        scheduleOne.setMaster(masterTwo);
        scheduleOne.setDtStart(newDtStart);
        scheduleOne.setDtEnd(newDtEnd);

        assertThrows(UpdatingDBDataException.class, () -> this.scheduleDao.update(scheduleOne));
    }

    @Test
    void shouldDeleteProperly() {
        this.scheduleDao.save(scheduleOne);

        this.scheduleDao.delete(scheduleOne);

        Optional<Schedule> schedule = this.scheduleDao.get(scheduleOne.getUuid());
        assertThrows(NoSuchElementException.class, schedule::orElseThrow);
    }

    @Test
    void shouldThrowWhileDeleteWithWrongUuid() {
        this.scheduleDao.save(scheduleOne);

        scheduleOne.setDtUpdate(LocalDateTime.now());

        assertThrows(DeletingDBDataException.class, () -> this.scheduleDao.delete(scheduleOne));
    }

}