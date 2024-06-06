package org.example.dao;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.entity.Schedule;
import org.example.core.entity.User;
import org.example.core.enums.ErrorType;
import org.example.core.util.NullCheckUtil;
import org.example.dao.api.IDataBaseConnection;
import org.example.dao.api.IScheduleDao;
import org.example.dao.api.IUserDao;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.ReceivingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ScheduleDao implements IScheduleDao {

    private static final String SCHEDULE_TABLE_NAME = "app.schedule";

    private static final String UUID_COLUMN_NAME = "uuid";

    private static final String MASTER_COLUMN_NAME = "master_uuid";

    private static final String DT_START_COLUMN_NAME = "dt_start";

    private static final String DT_END_COLUMN_NAME = "dt_end";

    private static final String DT_CREATE_COLUMN_NAME = "dt_create";

    private static final String DT_UPDATE_COLUMN_NAME = "dt_update";

    private static final String FAIL_CREATE_SCHEDULE_MESSAGE = "Ошибка создания графика";

    private static final String FAIL_RECEIVE_SINGLE_SCHEDULE_MESSAGE = "Ошибка получения графика";

    private static final String FAIL_RECEIVE_LIST_SCHEDULES_MESSAGE = "Ошибка получения списка графиков";

    private static final String FAIL_UPDATE_SCHEDULE_MESSAGE = "Ошибка обновления графика";

    private static final String FAIL_DELETE_SCHEDULE_MESSAGE = "Ошибка удаления графика";

    private static final String IMPOSSIBLE_GET_SCHEDULE_CAUSE_NULL = "Невозможно получить график так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_SAVE_SCHEDULE_CAUSE_NULL = "Невозможно создать график так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_UPDATE_SCHEDULE_CAUSE_NULL = "Невозможно обновить график так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_DELETE_SCHEDULE_CAUSE_NULL = "Невозможно удалить график так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_CHECK_IF_EXISTS_SCHEDULE_CAUSE_NULL = "Невозможно проверить существование графика так как в качестве аргумента был передан null";

    private static final String FAIL_CHECK_IF_SCHEDULE_EXISTS_MESSAGE = "Ошибка проверки существования графика";

    private final IUserDao userDao;

    private final IDataBaseConnection dataBaseConnection;

    public ScheduleDao(IUserDao userDao,
                       IDataBaseConnection dataBaseConnection) {
        this.userDao = userDao;
        this.dataBaseConnection = dataBaseConnection;
    }

    @Override
    public Optional<Schedule> get(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_SCHEDULE_CAUSE_NULL, uuid);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectOnePs = c.prepareStatement(createGetOneByUuidSqlStatement())) {
            Schedule schedule = null;

            selectOnePs.setObject(1, uuid);

            ResultSet rs = selectOnePs.executeQuery();

            while (rs.next()) {
                schedule = createSchedule(rs);
            }

            rs.close();

            return Optional.ofNullable(schedule);
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_SINGLE_SCHEDULE_MESSAGE)));
        }
    }


    @Override
    public List<Schedule> get() {
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectAllPs = c.prepareStatement(createGetAllSqlStatement())) {

            List<Schedule> schedules = new ArrayList<>();

            ResultSet rs = selectAllPs.executeQuery();

            while (rs.next()) {
                schedules.add(createSchedule(rs));
            }

            rs.close();

            return schedules;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_SCHEDULES_MESSAGE)));
        }
    }

    @Override
    public Schedule save(Schedule schedule) {
        NullCheckUtil.checkNull(IMPOSSIBLE_SAVE_SCHEDULE_CAUSE_NULL, schedule);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement insertSchedulePs = c.prepareStatement(createInsertSqlStatement())) {
            c.setAutoCommit(false);

            insertSchedule(schedule, insertSchedulePs);

            c.commit();

            return schedule;
        } catch (SQLException e) {
            throw new CreatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_CREATE_SCHEDULE_MESSAGE)));
        }
    }

    @Override
    public Schedule update(Schedule schedule) {
        NullCheckUtil.checkNull(IMPOSSIBLE_UPDATE_SCHEDULE_CAUSE_NULL, schedule);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement updateSchedulePs = c.prepareStatement(createUpdateSqlStatement())) {

            c.setAutoCommit(false);

            Schedule updateSchedule = updateSchedule(schedule, updateSchedulePs);

            c.commit();

            return updateSchedule;
        } catch (SQLException e) {
            throw new UpdatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SCHEDULE_MESSAGE)));
        }
    }

    @Override
    public boolean exists(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_CHECK_IF_EXISTS_SCHEDULE_CAUSE_NULL, uuid);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement existsPs = c.prepareStatement(createExistsSqlStatement())) {
            existsPs.setObject(1, uuid);
            return existsPs.execute();
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_CHECK_IF_SCHEDULE_EXISTS_MESSAGE)));
        }
    }

    @Override
    public void delete(Schedule schedule) {
        NullCheckUtil.checkNull(IMPOSSIBLE_DELETE_SCHEDULE_CAUSE_NULL, schedule);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createDeleteSqlStatement())) {
            c.setAutoCommit(false);

            deleteSchedule(schedule, ps);

            c.commit();
        } catch (SQLException e) {
            throw new DeletingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_SCHEDULE_MESSAGE)));
        }
    }

    private void deleteSchedule(Schedule schedule, PreparedStatement ps) throws SQLException {
        ps.setObject(1, schedule.getUuid());
        ps.setObject(2, schedule.getDtUpdate());

        if (ps.executeUpdate() < 1) {
            throw new DeletingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_SCHEDULE_MESSAGE)));
        }
    }

    private void insertSchedule(Schedule schedule, PreparedStatement insertSchedulePs) throws SQLException {
        insertSchedulePs.setObject(1, schedule.getUuid());
        LocalDateTime dtStart = schedule.getDtStart();
        if (dtStart == null) {
            insertSchedulePs.setNull(2, Types.NULL);
        } else {
            insertSchedulePs.setObject(2, dtStart);
        }
        LocalDateTime dtEnd = schedule.getDtEnd();
        if (dtEnd == null) {
            insertSchedulePs.setNull(3, Types.NULL);
        } else {
            insertSchedulePs.setObject(3, dtEnd);
        }
        insertSchedulePs.setObject(4, schedule.getDtEnd());
        insertSchedulePs.setObject(5, schedule.getDtCreate());
        insertSchedulePs.setObject(6, schedule.getDtUpdate());

        insertSchedulePs.execute();
    }

    private Schedule updateSchedule(Schedule schedule, PreparedStatement updateSchedulePs) throws SQLException {
        updateSchedulePs.setObject(1, schedule.getMaster().getUuid());
        LocalDateTime dtStart = schedule.getDtStart();
        if (dtStart == null) {
            updateSchedulePs.setNull(2, Types.NULL);
        } else {
            updateSchedulePs.setObject(2, dtStart);
        }
        LocalDateTime dtEnd = schedule.getDtEnd();
        if (dtEnd == null) {
            updateSchedulePs.setNull(3, Types.NULL);
        } else {
            updateSchedulePs.setObject(3, dtEnd);
        }
        updateSchedulePs.setObject(4, schedule.getUuid());
        updateSchedulePs.setObject(5, schedule.getDtUpdate());
        ResultSet rs = updateSchedulePs.executeQuery();
        Schedule updatedSchedule = null;
        while (rs.next()) {
            updatedSchedule = createSchedule(rs);
        }
        rs.close();
        return updatedSchedule;
    }

    private String createExistsSqlStatement() {
        StringBuilder sb = new StringBuilder("SELECT EXISTS ( SELECT 1 FROM ");
        sb.append(SCHEDULE_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ?)");
        return sb.toString();
    }

    private String createInsertSqlStatement() {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(SCHEDULE_TABLE_NAME);
        sb.append("(");
        sb.append(UUID_COLUMN_NAME);
        sb.append(", ");
        sb.append(MASTER_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_START_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_END_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(") VALUES (?, ?, ?, ?, ?, ?)");
        return sb.toString();
    }

    private String createGetAllSqlStatement() {
        StringBuilder sb = new StringBuilder("SELECT ");

        sb.append(UUID_COLUMN_NAME);
        sb.append(", ");
        sb.append(MASTER_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_START_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_END_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" FROM ");
        sb.append(SCHEDULE_TABLE_NAME);

        return sb.toString();
    }

    private String createGetOneByUuidSqlStatement() {
        StringBuilder sb = new StringBuilder(createGetAllSqlStatement());
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ?");
        return sb.toString();
    }

    private String createUpdateSqlStatement() {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(SCHEDULE_TABLE_NAME);
        sb.append(" SET ");
        sb.append(MASTER_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(DT_START_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(DT_END_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = NOW()");
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ? AND ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = ? RETURNING *");
        return sb.toString();
    }

    private String createDeleteSqlStatement() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(SCHEDULE_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ? AND ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = ?");
        return sb.toString();
    }

    private Schedule createSchedule(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);
        UUID rawMaster = (UUID) rs.getObject(MASTER_COLUMN_NAME);
        User master = this.userDao.get(rawMaster).orElseThrow();
        LocalDateTime dtStart = rs.getTimestamp(DT_START_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtEnd = rs.getTimestamp(DT_END_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtCreate = rs.getTimestamp(DT_CREATE_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtUpdate = rs.getTimestamp(DT_UPDATE_COLUMN_NAME).toLocalDateTime();

        return new Schedule(uuid, master, dtStart, dtEnd, dtCreate, dtUpdate);
    }
}
