package org.example.service.api;

import org.example.core.dto.UserCreateDto;
import org.example.core.entity.User;

import java.util.List;
import java.util.UUID;

public interface IUserService extends ICRUDService<User, UserCreateDto> {

    List<User> get(List<UUID> uuids);
}
