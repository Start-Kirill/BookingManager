package org.example.service;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.dto.ScheduleDto;
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
    public ScheduleDto get(UUID uuid) {
        return null;
    }

    @Override
    public List<ScheduleDto> get() {
        return null;
    }

    @Override
    public ScheduleDto save(ScheduleCreateDto scheduleCreateDto) {
        return null;
    }

    @Override
    public ScheduleDto update(ScheduleCreateDto scheduleCreateDto, UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }

    @Override
    public ScheduleDto delete(UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }
}
