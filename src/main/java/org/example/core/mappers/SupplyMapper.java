package org.example.core.mappers;

import org.example.core.dto.SupplyCreateDto;
import org.example.core.dto.SupplyDto;
import org.example.core.entity.Supply;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SupplyMapper {
    SupplyMapper INSTANCE = Mappers.getMapper(SupplyMapper.class);

    SupplyDto supplyToSupplyDto(Supply supply);

    Supply supplyCreateDtoToSupply(SupplyCreateDto dto);
}
