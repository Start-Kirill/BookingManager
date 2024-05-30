package org.example.service;

import org.example.core.dto.UserCreateDto;
import org.example.core.entity.User;
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
    public User get(UUID uuid) {
        return null;
    }

    @Override
    public List<User> get() {
        List<User> users = this.userDao.get();
//        return users.stream().map(u -> {
//            UserDto userDto = new UserDto();
//            userDto.setUuid(u.getUuid());
//            userDto.setName(u.getName());
//            userDto.setUserRole(u.getUserRole());
//            userDto.setPhoneNumber(u.getPhoneNumber());
//            List<UUID> supplies = u.getSupplies().stream().map(Supply::getUuid).toList();
//            userDto.setSupplies(supplies);
//            userDto.setDtCreate(u.getDtCreate());
//            userDto.setDtUpdate(u.getDtUpdate());
//            return userDto;
//        }).toList();
        return null;
    }

    @Override
    public User save(UserCreateDto userCreateDto) {
        return null;
    }

    @Override
    public User update(UserCreateDto userCreateDto, UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }

    @Override
    public void delete(UUID uuid, LocalDateTime dtUpdate) {

    }


}
