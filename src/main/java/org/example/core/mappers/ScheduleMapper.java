package org.example.core.mappers;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.dto.ScheduleDto;
import org.example.core.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScheduleMapper {

    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    Schedule scheduleCreateDtoToSchedule(ScheduleCreateDto dto);

    ScheduleDto scheduleToScheduleCreateDto(Schedule schedule);
}
