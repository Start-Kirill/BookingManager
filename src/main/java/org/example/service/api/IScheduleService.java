package org.example.service.api;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.dto.ScheduleDto;
import org.example.core.entity.Schedule;

public interface IScheduleService extends ICRUDService<Schedule, ScheduleCreateDto> {
}
