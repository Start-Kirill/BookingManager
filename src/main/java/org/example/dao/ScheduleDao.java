package org.example.dao;

import org.example.core.entity.Schedule;
import org.example.core.entity.User;
import org.example.dao.api.IScheduleDao;
import org.example.dao.api.IUserDao;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.ReceivingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;
import org.example.dao.factory.ds.DataBaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private final IUserDao userDao;

    public ScheduleDao(IUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<Schedule> get(UUID uuid) {
        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetOneByUuidSqlStatement())) {
            Schedule schedule = null;

            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                schedule = createSchedule(rs);
            }
            rs.close();
            return Optional.ofNullable(schedule);
        } catch (SQLException e) {
            throw new ReceivingDBDataException(FAIL_RECEIVE_SINGLE_SCHEDULE_MESSAGE, e.getCause());
        }
    }


    @Override
    public List<Schedule> get() {
        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetAllSqlStatement())) {
            List<Schedule> schedules = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                schedules.add(createSchedule(rs));
            }
            rs.close();
            return schedules;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(FAIL_RECEIVE_LIST_SCHEDULES_MESSAGE, e.getCause());
        }
    }

    @Override
    public Schedule save(Schedule schedule) {
        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createInsertSqlStatement())) {
            c.setAutoCommit(false);

            ps.setObject(1, schedule.getUuid());
            ps.setObject(2, schedule.getMaster().getUuid());
            ps.setObject(3, schedule.getDtStart());
            ps.setObject(4, schedule.getDtEnd());
            ps.setObject(5, schedule.getDtCreate());
            ps.setObject(6, schedule.getDtUpdate());

            ps.execute();

            c.commit();

            return schedule;
        } catch (SQLException e) {
            throw new CreatingDBDataException(FAIL_CREATE_SCHEDULE_MESSAGE, e.getCause());
        }
    }

    @Override
    public Schedule update(Schedule schedule) {
        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createUpdateSqlStatement())) {
            Schedule updatedSchedule = null;

            c.setAutoCommit(false);

            ps.setObject(1, schedule.getMaster().getUuid());
            ps.setObject(2, schedule.getDtStart());
            ps.setObject(3, schedule.getDtEnd());
            ps.setObject(4, schedule.getUuid());
            ps.setObject(5, schedule.getDtUpdate());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                updatedSchedule = createSchedule(rs);
            }

            c.commit();
            rs.close();

            return updatedSchedule;
        } catch (SQLException e) {
            throw new UpdatingDBDataException(FAIL_UPDATE_SCHEDULE_MESSAGE, e.getCause());
        }
    }

    @Override
    public void delete(Schedule schedule) {
        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createDeleteSqlStatement())) {
            c.setAutoCommit(false);

            ps.setObject(1, schedule.getUuid());
            ps.setObject(2, schedule.getDtUpdate());

            ps.execute();
            c.commit();
        } catch (SQLException e) {
            throw new DeletingDBDataException(FAIL_DELETE_SCHEDULE_MESSAGE, e.getCause());
        }
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
