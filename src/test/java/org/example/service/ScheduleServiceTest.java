package org.example.service;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.entity.Schedule;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.core.exceptions.NullArgumentException;
import org.example.dao.api.IScheduleDao;
import org.example.service.api.IUserService;
import org.example.service.exceptions.InvalidScheduleBodyException;
import org.example.service.exceptions.ObjectNotUpToDatedException;
import org.example.service.exceptions.SuchElementNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private IScheduleDao scheduleDao;

    @Mock
    private IUserService userService;

    @InjectMocks
    private ScheduleService scheduleService;

    private ScheduleCreateDto scheduleCreateDto;

    private Schedule schedule;

    private User master;

    private UUID uuid;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        now = LocalDateTime.now();
        UUID masterUuid = UUID.randomUUID();
        master = new User(masterUuid, "Kirill", "+13456789", UserRole.MASTER, new ArrayList<>(), now, now);
        scheduleCreateDto = new ScheduleCreateDto(masterUuid, now.plusDays(1), now.plusDays(2));
        schedule = new Schedule(uuid, master, now.plusDays(1), now.plusDays(2), now, now);
    }

    @Test
    void shouldGetByUuid() {
        when(scheduleDao.get(uuid)).thenReturn(Optional.of(schedule));
        when(scheduleDao.exists(uuid)).thenReturn(true);

        Schedule result = scheduleService.get(uuid);

        assertEquals(schedule, result);
        verify(scheduleDao).get(uuid);
    }

    @Test
    void shouldThrowWhileGetByUuid() {
        when(scheduleDao.exists(uuid)).thenReturn(false);

        assertThrows(SuchElementNotExistsException.class, () -> this.scheduleService.get(uuid));
    }

    @Test
    void shouldThrowWhileGetByNull() {
        assertThrows(NullArgumentException.class, () -> this.scheduleService.get(null));
    }

    @Test
    void shouldGetAll() {
        List<Schedule> scheduleList = List.of(schedule);
        when(scheduleDao.get()).thenReturn(scheduleList);

        List<Schedule> result = this.scheduleService.get();

        assertEquals(scheduleList, result);
        verify(scheduleDao, times(1)).get();
    }

    @Test
    void shouldExists() {
        when(scheduleDao.exists(schedule.getUuid())).thenReturn(true);

        assertTrue(scheduleService.exists(schedule.getUuid()));
        verify(scheduleDao, times(1)).exists(schedule.getUuid());
    }

    @Test
    void shouldNotExists() {
        when(scheduleDao.exists(schedule.getUuid())).thenReturn(false);

        assertFalse(scheduleService.exists(schedule.getUuid()));
        verify(scheduleDao, times(1)).exists(schedule.getUuid());
    }

    @ParameterizedTest
    @CsvSource({"1, 1", "1, null", "null, 1", "null, null"})
    void shouldSave(String rawDtStart, String rawDtEnd) {
        when(userService.get(any(UUID.class))).thenReturn(master);
        when(scheduleDao.save(any(Schedule.class))).thenReturn(schedule);

        LocalDateTime dtStart = "null".equals(rawDtStart) ? null : schedule.getDtStart();
        LocalDateTime dtEnd = "null".equals(rawDtEnd) ? null : schedule.getDtEnd();

        scheduleCreateDto.setDtStart(dtStart);
        scheduleCreateDto.setDtEnd(dtEnd);

        schedule.setDtStart(dtStart);
        schedule.setDtEnd(dtEnd);

        Schedule result = this.scheduleService.save(scheduleCreateDto);

        assertNotNull(result.getUuid());
        assertEquals(scheduleCreateDto.getMaster(), result.getMaster().getUuid());
        assertEquals(scheduleCreateDto.getDtStart(), result.getDtStart());
        assertEquals(scheduleCreateDto.getDtEnd(), result.getDtEnd());
        assertNotNull(result.getDtCreate());
        assertNotNull(result.getDtUpdate());

        verify(scheduleDao).save(any(Schedule.class));
    }

    @ParameterizedTest
    @CsvSource({"null, -1, -1", "1, 1, -1", "null, -1, 1"})
    void shouldThrowWhileSave(String rawMaster, String rawDtStart, String rawDtEnd) {
        UUID masterUuid = "null".equals(rawMaster) ? null : master.getUuid();
        LocalDateTime dtStart = "-1".equals(rawDtStart) ? now.minusDays(1) : schedule.getDtStart();
        LocalDateTime dtEnd = "-1".equals(rawDtEnd) ? now.minusDays(1) : schedule.getDtEnd();

        scheduleCreateDto.setMaster(masterUuid);
        scheduleCreateDto.setDtStart(dtStart);
        scheduleCreateDto.setDtEnd(dtEnd);

        assertThrows(InvalidScheduleBodyException.class, () -> this.scheduleService.save(scheduleCreateDto));
    }


    @ParameterizedTest
    @CsvSource({"1, 1", "1, null", "null, 1", "null, null"})
    void shouldUpdate(String rawDtStart, String rawDtEnd) {
        when(userService.get(any(UUID.class))).thenReturn(master);
        when(scheduleDao.update(any(Schedule.class))).thenReturn(schedule);
        when(scheduleDao.get(any(UUID.class))).thenReturn(Optional.of(schedule));
        when(scheduleDao.exists(any(UUID.class))).thenReturn(true);

        LocalDateTime dtStart = "null".equals(rawDtStart) ? null : schedule.getDtStart();
        LocalDateTime dtEnd = "null".equals(rawDtEnd) ? null : schedule.getDtEnd();

        scheduleCreateDto.setDtStart(dtStart);
        scheduleCreateDto.setDtEnd(dtEnd);

        schedule.setDtStart(dtStart);
        schedule.setDtEnd(dtEnd);

        Schedule result = this.scheduleService.update(scheduleCreateDto, schedule.getUuid(), schedule.getDtUpdate());

        assertNotNull(result.getUuid());
        assertEquals(scheduleCreateDto.getMaster(), result.getMaster().getUuid());
        assertEquals(scheduleCreateDto.getDtStart(), result.getDtStart());
        assertEquals(scheduleCreateDto.getDtEnd(), result.getDtEnd());
        assertNotNull(result.getDtCreate());
        assertNotNull(result.getDtUpdate());

        verify(scheduleDao).update(any(Schedule.class));
    }

    @ParameterizedTest
    @CsvSource({"null, -1, -1", "1, 1, -1", "null, -1, 1"})
    void shouldThrowWhileUpdate(String rawMaster, String rawDtStart, String rawDtEnd) {
        UUID masterUuid = "null".equals(rawMaster) ? null : master.getUuid();
        LocalDateTime dtStart = "-1".equals(rawDtStart) ? now.minusDays(1) : schedule.getDtStart();
        LocalDateTime dtEnd = "-1".equals(rawDtEnd) ? now.minusDays(1) : schedule.getDtEnd();

        scheduleCreateDto.setMaster(masterUuid);
        scheduleCreateDto.setDtStart(dtStart);
        scheduleCreateDto.setDtEnd(dtEnd);

        assertThrows(InvalidScheduleBodyException.class, () -> this.scheduleService.update(scheduleCreateDto, uuid, now));
    }

    @ParameterizedTest
    @CsvSource({"1, 1, null", "1, null, 1", "null, 1, 1"})
    void shouldThrowWhileUpdateNull(String dto, String rawUuid, String rawDtUpdate) {
        scheduleCreateDto = "null".equals(dto) ? null : scheduleCreateDto;
        uuid = "null".equals(rawUuid) ? null : uuid;
        LocalDateTime dtUpdate = "null".equals(rawDtUpdate) ? null : now;

        assertThrows(NullArgumentException.class, () -> this.scheduleService.update(scheduleCreateDto, uuid, dtUpdate));
    }

    @Test
    void shouldThrowWhileUpdateNotUpToDatedObject() {
        LocalDateTime dtUpdate = LocalDateTime.now().minusDays(1);
        when(scheduleDao.exists(uuid)).thenReturn(true);
        when(scheduleDao.get(uuid)).thenReturn(Optional.of(schedule));

        assertThrows(ObjectNotUpToDatedException.class, () -> scheduleService.update(scheduleCreateDto, uuid, dtUpdate));
    }

    @ParameterizedTest
    @CsvSource({"1, null", "null, 1", "null, null"})
    void shouldTrowWhileDeleteNull(String rawUuid, String rawDtUpdate) {
        UUID uuid = "null".equals(rawUuid) ? null : schedule.getUuid();
        LocalDateTime dtUpdate = "null".equals(rawDtUpdate) ? null : now;

        assertThrows(NullArgumentException.class, () -> scheduleService.delete(uuid, dtUpdate));
    }

    @Test
    void shouldThrowWhileDeleteNotUpToDatedObject() {
        LocalDateTime dtUpdate = LocalDateTime.now().minusDays(1);
        when(scheduleDao.exists(uuid)).thenReturn(true);
        when(scheduleDao.get(uuid)).thenReturn(Optional.of(schedule));

        assertThrows(ObjectNotUpToDatedException.class, () -> scheduleService.delete(uuid, dtUpdate));
    }
}