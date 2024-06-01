package org.example.service;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.entity.Schedule;
import org.example.core.mappers.ScheduleMapper;
import org.example.dao.api.IScheduleDao;
import org.example.service.api.IScheduleService;
import org.example.service.api.IUserService;
import org.example.service.exceptions.ObjectNotUpToDatedException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class ScheduleService implements IScheduleService {

    private static final String SCHEDULE_NOT_UP_TO_DATED_MESSAGE = "Изменяемый график не актуален. Получите новый график и попробуйте снова";

    private final IScheduleDao scheduleDao;

    private final IUserService userService;

    public ScheduleService(IScheduleDao scheduleDao,
                           IUserService userService) {
        this.scheduleDao = scheduleDao;
        this.userService = userService;
    }


    @Override
    public Schedule get(UUID uuid) {
        return this.scheduleDao.get(uuid).orElseThrow();
    }

    @Override
    public List<Schedule> get() {
        return this.scheduleDao.get();
    }

    @Override
    public Schedule save(ScheduleCreateDto scheduleCreateDto) {
        validate(scheduleCreateDto);

        Schedule schedule = ScheduleMapper.INSTANCE.scheduleCreateDtoToSchedule(scheduleCreateDto);

        schedule.setUuid(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        schedule.setDtCreate(now);
        schedule.setDtUpdate(now);

        UUID masterUuid = scheduleCreateDto.getMaster();
        schedule.setMaster(this.userService.get(masterUuid));

        return this.scheduleDao.save(schedule);
    }


    @Override
    public Schedule update(ScheduleCreateDto scheduleCreateDto, UUID uuid, LocalDateTime dtUpdate) {
        validate(scheduleCreateDto);

        Schedule actualSchedule = this.get(uuid);
        LocalDateTime actualDtUpdate = actualSchedule.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(SCHEDULE_NOT_UP_TO_DATED_MESSAGE);
        }

        actualSchedule.setMaster(this.userService.get(scheduleCreateDto.getMaster()));
        actualSchedule.setDtStart(scheduleCreateDto.getDtStart());
        actualSchedule.setDtEnd(scheduleCreateDto.getDtEnd());

        return this.scheduleDao.update(actualSchedule);
    }

    @Override
    public void delete(UUID uuid, LocalDateTime dtUpdate) {
        Schedule actualSchedule = this.get(uuid);
        LocalDateTime actualDtUpdate = actualSchedule.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(SCHEDULE_NOT_UP_TO_DATED_MESSAGE);
        }
        this.scheduleDao.delete(actualSchedule);
    }

    //    TODO
    private void validate(ScheduleCreateDto scheduleCreateDto) {

    }
}
