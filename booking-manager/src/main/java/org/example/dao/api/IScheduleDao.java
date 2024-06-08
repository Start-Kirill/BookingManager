package org.example.dao.api;

import org.example.core.entity.Schedule;

import java.util.UUID;

public interface IScheduleDao extends ICRUDDao<Schedule> {

    boolean exists(UUID uuid);
}
