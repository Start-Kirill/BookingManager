package org.example.service;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.entity.Schedule;
import org.example.dao.api.IScheduleDao;
import org.example.service.api.IScheduleService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ScheduleService implements IScheduleService {

    private final IScheduleDao scheduleDao;

    public ScheduleService(IScheduleDao scheduleDao) {
        this.scheduleDao = scheduleDao;
    }


    @Override
    public Schedule get(UUID uuid) {
        return null;
    }

    @Override
    public List<Schedule> get() {
        return null;
    }

    @Override
    public Schedule save(ScheduleCreateDto scheduleCreateDto) {
        return null;
    }

    @Override
    public Schedule update(ScheduleCreateDto scheduleCreateDto, UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }

    @Override
    public void delete(UUID uuid, LocalDateTime dtUpdate) {
    }
}
