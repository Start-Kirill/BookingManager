package org.example.core.mappers;

import org.example.core.dto.UserCreateDto;
import org.example.core.dto.UserDto;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "supplies", ignore = true)
    User userCreateDtoToUser(UserCreateDto dto);

    @Mapping(target = "supplies", qualifiedByName = "listSuppliesToListUuid")
    UserDto userToUserDto(User user);

    @Named("listSuppliesToListUuid")
    default List<UUID> listSuppliesToListUuid(List<Supply> supplies) {
        return supplies.stream().map(Supply::getUuid).toList();
    }
}
