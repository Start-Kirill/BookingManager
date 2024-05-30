package org.example.core.mappers;

import org.example.core.dto.UserCreateDto;
import org.example.core.dto.UserDto;
import org.example.core.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userCreateDtoToUser(UserCreateDto dto);

    UserDto userToUserDto(User user);
}
