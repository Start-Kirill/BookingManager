package org.example.service.api;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.entity.Schedule;

import java.util.UUID;

public interface IScheduleService extends ICRUDService<Schedule, ScheduleCreateDto> {
    boolean exists(UUID uuid);
}
