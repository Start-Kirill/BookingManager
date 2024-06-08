package org.example.service;

import org.example.core.dto.SupplyCreateDto;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.core.exceptions.NullArgumentException;
import org.example.dao.api.ISupplyDao;
import org.example.service.api.IUserService;
import org.example.service.exceptions.InvalidSupplyBodyException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyServiceTest {

    @Mock
    private ISupplyDao supplyDao;

    @Mock
    private IUserService userService;

    @InjectMocks
    private SupplyService supplyService;

    private SupplyCreateDto supplyCreateDto;

    private Supply supply;

    private User master;

    private UUID uuid;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        supplyService.setUserService(userService);
        uuid = UUID.randomUUID();
        UUID masterUuid = UUID.randomUUID();
        now = LocalDateTime.now();
        supplyCreateDto = new SupplyCreateDto("Test supply", BigDecimal.valueOf(100.5), 30, List.of(masterUuid));
        supply = new Supply(uuid, "Test supply", BigDecimal.valueOf(100.5), 30, new ArrayList<>(), now, now);
        master = new User(masterUuid, "Kirill", "+123465789", UserRole.MASTER, List.of(supply), now, now);
        supply.setMasters(List.of(master));
    }

    @Test
    void shouldGetByUuid() {
        when(supplyDao.get(uuid)).thenReturn(Optional.of(supply));
        when(supplyDao.exists(uuid)).thenReturn(true);

        Supply result = supplyService.get(uuid);

        assertEquals(supply, result);
        verify(supplyDao).get(uuid);
    }

    @Test
    void shouldThrowWhileGetByUuid() {
        when(supplyDao.exists(uuid)).thenReturn(false);

        assertThrows(SuchElementNotExistsException.class, () -> this.supplyService.get(uuid));
    }

    @Test
    void shouldThrowWhileGetByNull() {
        assertThrows(NullArgumentException.class, () -> this.supplyService.get((UUID) null));
    }

    @Test
    void shouldThrowWhileGetByListNull() {
        assertThrows(NullArgumentException.class, () -> this.supplyService.get((List) null));
    }

    @Test
    void shouldGetAll() {
        List<Supply> supplyList = List.of(supply);
        when(supplyDao.get()).thenReturn(supplyList);

        List<Supply> result = this.supplyService.get();

        assertEquals(supplyList, result);
        verify(supplyDao, times(1)).get();
    }

    @Test
    void shouldGetListByList() {
        List<Supply> supplyList = List.of(supply);
        List<UUID> uuids = List.of(supply.getUuid());
        when(supplyDao.exists(supply.getUuid())).thenReturn(true);
        when(supplyDao.get(uuids)).thenReturn(supplyList);

        List<Supply> result = this.supplyService.get(uuids);

        assertEquals(supplyList, result);
        verify(supplyDao, times(1)).exists(supply.getUuid());
        verify(supplyDao, times(1)).get(uuids);
    }

    @Test
    void shouldThrowWhileGetListByList() {
        List<UUID> uuids = List.of(supply.getUuid());
        when(supplyDao.exists(supply.getUuid())).thenReturn(false);

        assertThrows(SuchElementNotExistsException.class, () -> this.supplyService.get(uuids));
        verify(supplyDao, times(1)).exists(supply.getUuid());
    }

    @Test
    void shouldExists() {
        when(supplyDao.exists(supply.getUuid())).thenReturn(true);

        assertTrue(supplyService.exists(supply.getUuid()));
        verify(supplyDao, times(1)).exists(supply.getUuid());
    }

    @Test
    void shouldNotExists() {
        when(supplyDao.exists(supply.getUuid())).thenReturn(false);

        assertFalse(supplyService.exists(supply.getUuid()));
        verify(supplyDao, times(1)).exists(supply.getUuid());
    }

    @ParameterizedTest
    @CsvSource({"30, 1", "null, 1", "30, null", "null, null"})
    void shouldSave(String rawDuration, String rawMasters) {
        Integer duration = "null".equals(rawDuration) ? null : Integer.valueOf(rawDuration);
        List<UUID> masters = "null".equals(rawMasters) ? null : supplyCreateDto.getMasters();
        if (masters == null) {
            supply.setMasters(null);
        }
        supply.setDuration(duration);
        supplyCreateDto.setDuration(duration);
        supplyCreateDto.setMasters(masters);
        when(supplyDao.save(any(Supply.class))).thenReturn(supply);

        Supply result = this.supplyService.save(supplyCreateDto);

        assertNotNull(result.getUuid());
        assertEquals(supplyCreateDto.getName(), result.getName());
        assertEquals(supplyCreateDto.getDuration(), result.getDuration());
        List<UUID> masterUuids = null;
        if (result.getMasters() != null) {
            masterUuids = result.getMasters().stream().map(User::getUuid).toList();
        }
        assertEquals(supplyCreateDto.getMasters(), masterUuids);
        assertEquals(supplyCreateDto.getPrice(), supply.getPrice());
        verify(supplyDao, times(1)).save(any(Supply.class));
    }

    @ParameterizedTest
    @CsvSource({"Test supply, null", "null, 100.5", "null, null"})
    void shouldThrowWhileSave(String name, String rawPrice) {
        name = "null".equals(name) ? null : name;
        BigDecimal price = "null".equals(rawPrice) ? null : new BigDecimal(rawPrice);
        supplyCreateDto.setName(name);
        supplyCreateDto.setPrice(price);

        assertThrows(InvalidSupplyBodyException.class, () -> this.supplyService.save(supplyCreateDto));
    }

    @ParameterizedTest
    @CsvSource({"30, 1", "null, 1", "30, null", "null, null"})
    void shouldUpdate(String rawDuration, String rawMasters) {
        Integer duration = "null".equals(rawDuration) ? null : Integer.valueOf(rawDuration);
        List<UUID> masters = "null".equals(rawMasters) ? null : supplyCreateDto.getMasters();
        if (masters == null) {
            supply.setMasters(null);
        }
        supply.setDuration(duration);
        supplyCreateDto.setDuration(duration);
        supplyCreateDto.setMasters(masters);
        when(supplyDao.get(uuid)).thenReturn(Optional.of(supply));
        lenient().when(userService.get(anyList())).thenReturn(supply.getMasters());
        when(supplyDao.update(any(Supply.class))).thenReturn(supply);
        when(supplyDao.exists(supply.getUuid())).thenReturn(true);

        Supply result = this.supplyService.update(supplyCreateDto, supply.getUuid(), supply.getDtUpdate());

        assertNotNull(result.getUuid());
        assertEquals(supplyCreateDto.getName(), result.getName());
        assertEquals(supplyCreateDto.getDuration(), result.getDuration());
        List<UUID> masterUuids = null;
        if (!result.getMasters().isEmpty()) {
            masterUuids = result.getMasters().stream().map(User::getUuid).toList();
        }
        assertEquals(supplyCreateDto.getMasters(), masterUuids);
        assertEquals(supplyCreateDto.getPrice(), supply.getPrice());
        verify(supplyDao, times(1)).update(any(Supply.class));
    }

    @ParameterizedTest
    @CsvSource({"Test supply, null", "null, 100.5", "null, null"})
    void shouldThrowWhileUpdate(String name, String rawPrice) {
        name = "null".equals(name) ? null : name;
        BigDecimal price = "null".equals(rawPrice) ? null : new BigDecimal(rawPrice);
        supplyCreateDto.setName(name);
        supplyCreateDto.setPrice(price);

        assertThrows(InvalidSupplyBodyException.class, () -> this.supplyService.update(supplyCreateDto, uuid, now));
    }

    @ParameterizedTest
    @CsvSource({"1, 1, null", "1, null, 1", "null, 1, 1"})
    void shouldThrowWhileUpdateNull(String dto, String rawUuid, String rawDtUpdate) {
        supplyCreateDto = "null".equals(dto) ? null : supplyCreateDto;
        uuid = "null".equals(rawUuid) ? null : uuid;
        LocalDateTime dtUpdate = "null".equals(rawDtUpdate) ? null : now;

        assertThrows(NullArgumentException.class, () -> this.supplyService.update(supplyCreateDto, uuid, dtUpdate));
    }

    @Test
    void shouldThrowWhileUpdateNotUpToDatedObject() {
        LocalDateTime dtUpdate = LocalDateTime.now().minusDays(1);
        when(supplyDao.exists(uuid)).thenReturn(true);
        when(supplyDao.get(uuid)).thenReturn(Optional.of(supply));

        assertThrows(ObjectNotUpToDatedException.class, () -> supplyService.update(supplyCreateDto, uuid, dtUpdate));
    }

    @ParameterizedTest
    @CsvSource({"1, null", "null, 1", "null, null"})
    void shouldTrowWhileDeleteNull(String rawUuid, String rawDtUpdate) {
        UUID uuid = "null".equals(rawUuid) ? null : supply.getUuid();
        LocalDateTime dtUpdate = "null".equals(rawDtUpdate) ? null : now;

        assertThrows(NullArgumentException.class, () -> supplyService.delete(uuid, dtUpdate));
    }

    @Test
    void shouldThrowWhileDeleteNotUpToDatedObject() {
        UUID uuid = supply.getUuid();
        LocalDateTime dtUpdate = LocalDateTime.now().minusDays(1);
        when(supplyDao.exists(uuid)).thenReturn(true);
        when(supplyDao.get(uuid)).thenReturn(Optional.of(supply));

        assertThrows(ObjectNotUpToDatedException.class, () -> supplyService.delete(uuid, dtUpdate));
    }


}