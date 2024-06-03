package org.example.dao;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.entity.Supply;
import org.example.core.enums.ErrorType;
import org.example.core.util.NullCheckUtil;
import org.example.dao.api.IDataBaseConnection;
import org.example.dao.api.ISupplyDao;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.ReceivingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SupplyDao implements ISupplyDao {

    private static final String SUPPLY_TABLE_NAME = "app.supply";

    private static final String UUID_COLUMN_NAME = "uuid";

    private static final String NAME_COLUMN_NAME = "name";

    private static final String PRICE_COLUMN_NAME = "price";

    private static final String DURATION_COLUMN_NAME = "duration";

    private static final String DT_CREATE_COLUMN_NAME = "dt_create";

    private static final String DT_UPDATE_COLUMN_NAME = "dt_update";

    private static final String FAIL_RECEIVE_SINGLE_SUPPLY_MESSAGE = "Ошибка получения услуги";

    private static final String FAIL_RECEIVE_LIST_SUPPLIES_MESSAGE = "Ошибка получения списка услуг";

    private static final String FAIL_CREATE_SUPPLY_MESSAGE = "Ошибка сохранения данных в базу";

    private static final String FAIL_UPDATE_SUPPLY_MESSAGE = "Ошибка обновления данных";

    private static final String FAIL_DELETE_SUPPLY_MESSAGE = "Ошибка удаления данных";

    private static final String IMPOSSIBLE_GET_SUPPLY_CAUSE_NULL = "Невозможно получить услугу так как в качестве аргументы был передан null";

    private static final String IMPOSSIBLE_GET_LIST_OF_SUPPLIES_CAUSE_NULL = "Невозможно получить список услуг так как в качестве аргументы был передан null";

    private static final String IMPOSSIBLE_SAVE_SUPPLY_CAUSE_NULL = "Невозможно создать услугу так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_UPDATE_SUPPLY_CAUSE_NULL = "Невозможно обновить услугу так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_DELETE_SUPPLY_CAUSE_NULL = "Невозможно удалить услугу так как в качестве аргумента был передан null";

    private final IDataBaseConnection dataBaseConnection;

    public SupplyDao(IDataBaseConnection dataBaseConnection) {
        this.dataBaseConnection = dataBaseConnection;
    }

    @Override
    public Optional<Supply> get(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_SUPPLY_CAUSE_NULL, uuid);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetOneByUuidSqlStatement())) {
            Supply supply = null;

            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                supply = createSupply(rs);
            }
            rs.close();
            return Optional.ofNullable(supply);
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_SINGLE_SUPPLY_MESSAGE)));
        }

    }

    @Override
    public List<Supply> get() {

        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetAllSqlStatement())) {
            List<Supply> supplies = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                supplies.add(createSupply(rs));
            }
            rs.close();
            return supplies;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_SUPPLIES_MESSAGE)));
        }
    }

    @Override
    public List<Supply> get(List<UUID> uuids) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_LIST_OF_SUPPLIES_CAUSE_NULL, uuids);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetAccordingToUuidsSqlStatement(uuids.size()))) {

            for (int i = 0; i < uuids.size(); i++) {
                ps.setObject(i + 1, uuids.get(i));
            }

            List<Supply> supplies = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                supplies.add(createSupply(rs));
            }
            rs.close();
            return supplies;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_SUPPLIES_MESSAGE)));
        }
    }


    @Override
    public Supply save(Supply supply) {
        NullCheckUtil.checkNull(IMPOSSIBLE_SAVE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps1 = c.prepareStatement(createInsertSqlStatement())) {
            c.setAutoCommit(false);

            ps1.setObject(1, supply.getUuid());
            ps1.setString(2, supply.getName());
            ps1.setBigDecimal(3, supply.getPrice());
            Integer duration = supply.getDuration();
            if (duration != null) {
                ps1.setInt(4, supply.getDuration());
            } else {
                ps1.setNull(4, Types.NULL);
            }
            ps1.setObject(5, supply.getDtCreate());
            ps1.setObject(6, supply.getDtUpdate());

            ps1.execute();

            c.commit();

            return supply;
        } catch (SQLException e) {
            throw new CreatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_CREATE_SUPPLY_MESSAGE)));
        }


    }

    @Override
    public Supply update(Supply supply) {
        NullCheckUtil.checkNull(IMPOSSIBLE_UPDATE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createUpdateSqlStatement())) {
            Supply updatedSupply = null;

            c.setAutoCommit(false);

            ps.setString(1, supply.getName());
            ps.setBigDecimal(2, supply.getPrice());
            Integer duration = supply.getDuration();
            if (duration != null) {
                ps.setInt(3, supply.getDuration());
            } else {
                ps.setNull(3, Types.NULL);
            }
            ps.setObject(4, supply.getUuid());
            ps.setObject(5, supply.getDtUpdate());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                updatedSupply = createSupply(rs);
            }

            if (updatedSupply == null) {
                throw new UpdatingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
            }

            c.commit();
            rs.close();

            return updatedSupply;
        } catch (SQLException e) {
            throw new UpdatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
        }
    }

    @Override
    public void delete(Supply supply) {
        NullCheckUtil.checkNull(IMPOSSIBLE_DELETE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createDeleteSqlStatement())) {
            c.setAutoCommit(false);

            ps.setObject(1, supply.getUuid());
            ps.setObject(2, supply.getDtUpdate());

            if (ps.executeUpdate() < 1) {
                throw new DeletingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_SUPPLY_MESSAGE)));
            }
            c.commit();
        } catch (SQLException e) {
            throw new DeletingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_SUPPLY_MESSAGE)));
        }
    }

    private String createInsertSqlStatement() {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(SUPPLY_TABLE_NAME);
        sb.append("(");
        sb.append(UUID_COLUMN_NAME);
        sb.append(", ");
        sb.append(NAME_COLUMN_NAME);
        sb.append(", ");
        sb.append(PRICE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DURATION_COLUMN_NAME);
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
        sb.append(NAME_COLUMN_NAME);
        sb.append(", ");
        sb.append(PRICE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DURATION_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" FROM ");
        sb.append(SUPPLY_TABLE_NAME);

        return sb.toString();
    }

    private String createGetAccordingToUuidsSqlStatement(int number) {
        StringBuilder sb = new StringBuilder(createGetAllSqlStatement());
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" IN (");
        boolean needComma = false;
        for (int i = 0; i < number; i++) {
            if (needComma) {
                sb.append(", ");
            }
            sb.append("?");
            needComma = true;
        }
        sb.append(")");
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
        sb.append(SUPPLY_TABLE_NAME);
        sb.append(" SET ");
        sb.append(NAME_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(PRICE_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(DURATION_COLUMN_NAME);
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
        sb.append(SUPPLY_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ? AND ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = ?");
        return sb.toString();
    }

    private Supply createSupply(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);
        String name = rs.getString(NAME_COLUMN_NAME);
        BigDecimal price = rs.getBigDecimal(PRICE_COLUMN_NAME);
        Integer duration = (Integer) rs.getObject(DURATION_COLUMN_NAME);
        LocalDateTime dtCreate = rs.getTimestamp(DT_CREATE_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtUpdate = rs.getTimestamp(DT_UPDATE_COLUMN_NAME).toLocalDateTime();
        return new Supply(uuid, name, price, duration, dtCreate, dtUpdate);
    }

}
