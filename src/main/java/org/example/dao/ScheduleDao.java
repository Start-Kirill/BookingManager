package org.example.dao;

import org.example.core.entity.Schedule;
import org.example.dao.api.IScheduleDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScheduleDao implements IScheduleDao {


    @Override
    public Optional<Schedule> get(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public List<Schedule> get() {
        return null;
    }

    @Override
    public Schedule save(Schedule schedule) {
        return null;
    }

    @Override
    public Schedule update(Schedule schedule) {
        return null;
    }

    @Override
    public void delete(Schedule schedule) {

    }
}
