package org.example.service;

import org.example.core.dto.UserCreateDto;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.core.exceptions.NullArgumentException;
import org.example.dao.api.ICRUDDao;
import org.example.service.api.ISupplyService;
import org.example.service.exceptions.InvalidUserBodyException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private ICRUDDao<User> userDao;

    @Mock
    private ISupplyService supplyService;

    @InjectMocks
    private UserService userService;

    private User user;

    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto("Kirill", "+123456789", UserRole.MASTER, new ArrayList<>());
        user = new User(UUID.randomUUID(), "Kirill", "+123456789", UserRole.MASTER, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now());
    }


    @Test
    void shouldGetByUuid() {
        UUID uuid = mock(UUID.class);
        when(userDao.get(uuid)).thenReturn(Optional.of(user));

        User result = this.userService.get(uuid);

        assertEquals(user, result);
        verify(userDao, times(2)).get(uuid);
    }

    @Test
    void shouldThrowWhileGetByUuid() {
        UUID uuid = mock(UUID.class);
        when(userDao.get(uuid)).thenReturn(Optional.empty());

        assertThrows(SuchElementNotExistsException.class, () -> this.userService.get(uuid));
        verify(userDao, times(1)).get(uuid);
    }

    @Test
    void shouldThrowWhileGetByNull() {
        assertThrows(NullArgumentException.class, () -> this.userService.get((UUID) null));
    }

    @Test
    void shouldThrowWhileGetByListNull() {
        assertThrows(NullArgumentException.class, () -> this.userService.get((List) null));
    }


    @Test
    void shouldGetAll() {
        List<User> userList = List.of(user);
        when(userDao.get()).thenReturn(userList);

        List<User> result = this.userService.get();

        assertEquals(userList, result);
        verify(userDao, times(1)).get();
    }

    @Test
    void shouldGetListByList() {
        List<User> userList = List.of(user);
        List<UUID> uuids = List.of(user.getUuid());
        when(userDao.get(user.getUuid())).thenReturn(Optional.of(user));

        List<User> result = this.userService.get(uuids);

        assertEquals(userList, result);
        verify(userDao, times(2)).get(user.getUuid());
    }

    @Test
    void shouldThrowWhileGetListByList() {
        List<UUID> uuids = List.of(user.getUuid());
        when(userDao.get(user.getUuid())).thenReturn(Optional.empty());

        assertThrows(SuchElementNotExistsException.class, () -> this.userService.get(uuids));
        verify(userDao, times(1)).get(user.getUuid());
    }


    @Test
    void shouldExists() {
        when(userDao.get(user.getUuid())).thenReturn(Optional.of(user));

        assertTrue(userService.exists(user.getUuid()));
        verify(userDao, times(1)).get(user.getUuid());
    }

    @Test
    void shouldNotExists() {
        when(userDao.get(user.getUuid())).thenReturn(Optional.empty());

        assertFalse(userService.exists(user.getUuid()));
        verify(userDao, times(1)).get(user.getUuid());
    }

    @ParameterizedTest
    @CsvSource({"+123456789", "null"})
    void shouldSave(String phone) {
        phone = "null".equals(phone) ? null : phone;
        userCreateDto.setPhoneNumber(phone);
        user.setPhoneNumber(phone);
        when(userDao.save(any(User.class))).thenReturn(user);

        User result = this.userService.save(userCreateDto);

        assertNotNull(result.getUuid());
        assertEquals(userCreateDto.getName(), result.getName());
        assertEquals(userCreateDto.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(userCreateDto.getUserRole(), result.getUserRole());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    void shouldSaveWithNullSupplies() {
        userCreateDto.setSupplies(null);
        user.setSupplies(null);
        when(userDao.save(any(User.class))).thenReturn(user);

        User result = this.userService.save(userCreateDto);

        assertNotNull(result.getUuid());
        assertEquals(userCreateDto.getName(), result.getName());
        assertEquals(userCreateDto.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(userCreateDto.getUserRole(), result.getUserRole());
        verify(userDao, times(1)).save(any(User.class));
    }

    @ParameterizedTest
    @CsvSource({"MASTER, null, +123456789", "null, Kirill, +123456789", "null, null, +123456789", "MASTER, Kirill, hello"})
    void shouldThrowWhileSaveWithNull(String role, String name, String phone) {
        UserRole userRole = "null".equals(role) ? null : UserRole.fromString(role);
        name = "null".equals(name) ? null : name;
        userCreateDto.setUserRole(userRole);
        userCreateDto.setName(name);
        userCreateDto.setPhoneNumber(phone);

        assertThrows(InvalidUserBodyException.class, () -> this.userService.save(userCreateDto));
    }

    @ParameterizedTest
    @CsvSource({"Volodya, +987654321, ADMIN, 1", "Volodya, null, ADMIN, null"})
    void shouldUpdate(String name, String phone, String role, String supply) {
        UUID userId = user.getUuid();
        LocalDateTime dtUpdate = user.getDtUpdate();
        when(userDao.get(userId)).thenReturn(Optional.of(user));
        when(userDao.update(any(User.class))).thenReturn(user);

        UserRole userRole = "null".equals(role) ? null : UserRole.fromString(role);
        name = "null".equals(name) ? null : name;
        phone = "null".equals(phone) ? null : phone;
        List<UUID> supplies = "null".equals(supply) ? null : List.of(UUID.randomUUID());

        userCreateDto.setSupplies(supplies);
        userCreateDto.setUserRole(userRole);
        userCreateDto.setName(name);
        userCreateDto.setPhoneNumber(phone);

        User result = userService.update(userCreateDto, userId, dtUpdate);

        assertEquals(user.getName(), result.getName());
        assertEquals(user.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(user.getUserRole(), result.getUserRole());
        verify(userDao, times(1)).update(any(User.class));
    }

    @ParameterizedTest
    @CsvSource({"1, 1, null", "1, null, 1", "null, 1, 1"})
    void shouldThrowWhileUpdateNull(String dto, String rawUuid, String rawDtUpdate) {
        userCreateDto = "null".equals(dto) ? null : userCreateDto;
        UUID uuid = "null".equals(rawUuid) ? null : UUID.randomUUID();
        LocalDateTime dtUpdate = "null".equals(rawDtUpdate) ? null : LocalDateTime.now();

        assertThrows(NullArgumentException.class, () -> this.userService.update(userCreateDto, uuid, dtUpdate));
    }

    @ParameterizedTest
    @CsvSource({"null, +987654321, ADMIN, 1", "Volodya, +987654321, null, 1"})
    void shouldTrowWhileUpdate(String name, String phone, String role, String supply) {
        UUID uuid = user.getUuid();
        LocalDateTime dtUpdate = user.getDtUpdate();

        UserRole userRole = "null".equals(role) ? null : UserRole.fromString(role);
        name = "null".equals(name) ? null : name;
        phone = "null".equals(phone) ? null : phone;
        List<UUID> supplies = "null".equals(supply) ? null : List.of(UUID.randomUUID());

        userCreateDto.setSupplies(supplies);
        userCreateDto.setUserRole(userRole);
        userCreateDto.setName(name);
        userCreateDto.setPhoneNumber(phone);

        assertThrows(InvalidUserBodyException.class, () -> this.userService.update(userCreateDto, uuid, dtUpdate));
    }

    @Test
    void shouldThrowWhileUpdateNotUpToDatedObject() {
        UUID uuid = user.getUuid();
        LocalDateTime dtUpdate = LocalDateTime.now().minusDays(1);
        when(userDao.get(uuid)).thenReturn(Optional.of(user));

        assertThrows(ObjectNotUpToDatedException.class, () -> userService.update(userCreateDto, uuid, dtUpdate));
    }


    @Test
    void shouldDelete() {
        UUID uuid = user.getUuid();
        LocalDateTime dtUpdate = user.getDtUpdate();
        when(userDao.get(uuid)).thenReturn(Optional.of(user));

        userService.delete(uuid, dtUpdate);

        verify(userDao, times(1)).delete(user);
    }

    @ParameterizedTest
    @CsvSource({"1, null", "null, 1", "null, null"})
    void shouldTrowWhileDeleteNull(String rawUuid, String rawDtUpdate) {
        UUID uuid = "null".equals(rawUuid) ? null : UUID.randomUUID();
        LocalDateTime dtUpdate = "null".equals(rawDtUpdate) ? null : LocalDateTime.now();

        assertThrows(NullArgumentException.class, () -> userService.delete(uuid, dtUpdate));
    }

    @Test
    void shouldThrowWhileDeleteNotUpToDatedObject() {
        UUID uuid = user.getUuid();
        LocalDateTime dtUpdate = LocalDateTime.now().minusDays(1);
        when(userDao.get(uuid)).thenReturn(Optional.of(user));

        assertThrows(ObjectNotUpToDatedException.class, () -> userService.delete(uuid, dtUpdate));
    }


}