package org.example.core.mappers;

import org.example.core.dto.SupplyCreateDto;
import org.example.core.dto.SupplyDto;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@Mapper
public interface SupplyMapper {
    SupplyMapper INSTANCE = Mappers.getMapper(SupplyMapper.class);

    @Mapping(target = "masters", qualifiedByName = "listUsersToListUuid")
    SupplyDto supplyToSupplyDto(Supply supply);

    @Mapping(target = "masters", ignore = true)
    Supply supplyCreateDtoToSupply(SupplyCreateDto dto);

    @Named("listUsersToListUuid")
    default List<UUID> listUsersToListUuid(List<User> users) {
        return users.stream().map(User::getUuid).toList();
    }
}
