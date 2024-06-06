package org.example.service;

import org.example.core.dto.ScheduleCreateDto;
import org.example.core.dto.errors.ErrorResponse;
import org.example.core.entity.Schedule;
import org.example.core.enums.ErrorType;
import org.example.core.mappers.ScheduleMapper;
import org.example.core.util.NullCheckUtil;
import org.example.dao.api.IScheduleDao;
import org.example.service.api.IScheduleService;
import org.example.service.api.IUserService;
import org.example.service.exceptions.InvalidScheduleBodyException;
import org.example.service.exceptions.ObjectNotUpToDatedException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScheduleService implements IScheduleService {

    private static final String DT_START_FIELD_NAME = "dt_start";

    private static final String DT_END_FIELD_NAME = "dt_end";

    private static final String MASTER_FIELD_NAME = "dt_end";

    private static final String MASTER_CAN_NOT_BE_NULL_MESSAGE = "Поле Master должно быть заполнено";

    private static final String SCHEDULE_NOT_UP_TO_DATED_MESSAGE = "Изменяемый график не актуален. Получите новый график и попробуйте снова";

    private static final String DATE_CAN_NOT_BE_BEFORE_NOW_MESSAGE = "Дата и время утанавливаемого графика не может быть в прошлом.";

    private static final String START_DATE_CAN_NOT_BE_AFTER_END_DATE = "Дата и время начала не может быть после даты окончания";

    private static final String IMPOSSIBLE_GET_SCHEDULE_CAUSE_NULL = "Невозможно получить график так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_SAVE_SCHEDULE_CAUSE_NULL = "Невозможно создать график так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_UPDATE_SCHEDULE_CAUSE_NULL = "Невозможно обновить график так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_DELETE_SCHEDULE_CAUSE_NULL = "Невозможно удалить график так как в качестве аргумента был передан null";

    private final IScheduleDao scheduleDao;

    private final IUserService userService;

    public ScheduleService(IScheduleDao scheduleDao,
                           IUserService userService) {
        this.scheduleDao = scheduleDao;
        this.userService = userService;
    }


    @Override
    public Schedule get(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_SCHEDULE_CAUSE_NULL, uuid);
        return this.scheduleDao.get(uuid).orElseThrow();
    }

    @Override
    public List<Schedule> get() {
        return this.scheduleDao.get();
    }

    @Override
    public Schedule save(ScheduleCreateDto scheduleCreateDto) {
        NullCheckUtil.checkNull(IMPOSSIBLE_SAVE_SCHEDULE_CAUSE_NULL, scheduleCreateDto);
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
        NullCheckUtil.checkNull(IMPOSSIBLE_UPDATE_SCHEDULE_CAUSE_NULL, scheduleCreateDto, uuid, dtUpdate);
        validate(scheduleCreateDto);

        Schedule actualSchedule = this.get(uuid);
        LocalDateTime actualDtUpdate = actualSchedule.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(List.of(new ErrorResponse(ErrorType.ERROR, SCHEDULE_NOT_UP_TO_DATED_MESSAGE)));
        }

        actualSchedule.setMaster(this.userService.get(scheduleCreateDto.getMaster()));
        actualSchedule.setDtStart(scheduleCreateDto.getDtStart());
        actualSchedule.setDtEnd(scheduleCreateDto.getDtEnd());

        return this.scheduleDao.update(actualSchedule);
    }

    @Override
    public void delete(UUID uuid, LocalDateTime dtUpdate) {
        NullCheckUtil.checkNull(IMPOSSIBLE_DELETE_SCHEDULE_CAUSE_NULL, uuid, dtUpdate);
        Schedule actualSchedule = this.get(uuid);
        LocalDateTime actualDtUpdate = actualSchedule.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(List.of(new ErrorResponse(ErrorType.ERROR, SCHEDULE_NOT_UP_TO_DATED_MESSAGE)));
        }
        this.scheduleDao.delete(actualSchedule);
    }

    private void validate(ScheduleCreateDto scheduleCreateDto) {
        Map<String, String> errors = new HashMap<>();

        UUID master = scheduleCreateDto.getMaster();
        if (master == null) {
            errors.put(MASTER_FIELD_NAME, MASTER_CAN_NOT_BE_NULL_MESSAGE);
        }

        LocalDateTime dtStart = scheduleCreateDto.getDtStart();
        LocalDateTime dtEnd = scheduleCreateDto.getDtEnd();
        LocalDateTime now = LocalDateTime.now();
        if (dtStart != null && dtStart.isBefore(now)) {
            errors.put(DT_START_FIELD_NAME, DATE_CAN_NOT_BE_BEFORE_NOW_MESSAGE);
        }
        if (dtEnd != null && dtEnd.isBefore(now)) {
            errors.put(DT_END_FIELD_NAME, DATE_CAN_NOT_BE_BEFORE_NOW_MESSAGE);
        }
        if (dtStart != null && dtEnd != null && dtStart.isAfter(dtEnd)) {
            errors.put(DT_START_FIELD_NAME, START_DATE_CAN_NOT_BE_AFTER_END_DATE);
        }

        if (!errors.isEmpty()) {
            throw new InvalidScheduleBodyException(errors);
        }
    }

    @Override
    public boolean exists(UUID uuid) {
        return this.scheduleDao.exists(uuid);
    }
}
