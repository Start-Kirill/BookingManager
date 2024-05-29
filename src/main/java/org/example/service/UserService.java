package org.example.service;

import org.example.core.dto.UserCreateDto;
import org.example.core.dto.UserDto;
import org.example.dao.api.IUserDao;
import org.example.service.api.IUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserService implements IUserService {

    private final IUserDao userDao;

    public UserService(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto get(UUID uuid) {
        return null;
    }

    @Override
    public List<UserDto> get() {
        return null;
    }

    @Override
    public UserDto save(UserCreateDto userCreateDto) {
        return null;
    }

    @Override
    public UserDto update(UserCreateDto userCreateDto, UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }

    @Override
    public UserDto delete(UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }
}
