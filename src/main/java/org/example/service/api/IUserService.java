package org.example.service.api;

import org.example.core.dto.UserCreateDto;
import org.example.core.entity.User;

public interface IUserService extends ICRUDService<User, UserCreateDto> {
}
