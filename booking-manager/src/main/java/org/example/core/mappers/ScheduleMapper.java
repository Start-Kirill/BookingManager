package org.example.core.mappers;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.dto.ScheduleDto;
import org.example.core.entity.Schedule;
import org.example.core.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface ScheduleMapper {

    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    @Mapping(target = "master", ignore = true)
    Schedule scheduleCreateDtoToSchedule(ScheduleCreateDto dto);

    @Mapping(target = "master", qualifiedByName = "userToUuid")
    ScheduleDto scheduleToScheduleDto(Schedule schedule);

    @Named("userToUuid")
    default UUID userToUuid(User user) {
        return user.getUuid();
    }
}
