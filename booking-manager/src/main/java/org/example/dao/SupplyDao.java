package org.example.dao;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.ErrorType;
import org.example.core.enums.UserRole;
import org.example.core.util.NullCheckUtil;
import org.example.dao.api.ICRUDDao;
import org.example.dao.api.IDataBaseConnection;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.ReceivingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class SupplyDao implements ICRUDDao<Supply> {

    private static final String USERS_SUPPLY_TABLE_NAME = "app.users_supply";

    private static final String USERS_SUPPLY_USER_COLUMN_NAME = "user_uuid";

    private static final String USERS_SUPPLY_SUPPLY_COLUMN_NAME = "supply_uuid";

    private static final String SUPPLY_TABLE_NAME = "app.supply";

    private static final String USERS_TABLE_NAME = "app.users";

    private static final String UUID_COLUMN_NAME = "uuid";

    private static final String NAME_COLUMN_NAME = "name";

    private static final String DT_CREATE_COLUMN_NAME = "dt_create";

    private static final String DT_UPDATE_COLUMN_NAME = "dt_update";

    private static final String SUPPLY_UUID_COLUMN_NAME = "supply.uuid";

    private static final String SUPPLY_NAME_COLUMN_NAME = "supply.name";

    private static final String PRICE_COLUMN_NAME = "price";

    private static final String DURATION_COLUMN_NAME = "duration";

    private static final String SUPPLY_DT_CREATE_COLUMN_NAME = "supply.dt_create";

    private static final String SUPPLY_DT_UPDATE_COLUMN_NAME = "supply.dt_update";

    private static final String USERS_UUID_COLUMN_NAME = "users.uuid";

    private static final String USERS_NAME_COLUMN_NAME = "users.name";

    private static final String ALIAS_USERS_NAME_COLUMN_NAME = "u_name";

    private static final String PHONE_NUMBER_COLUMN_NAME = "phone_number";

    private static final String USER_ROLE_COLUMN_NAME = "role";

    private static final String USERS_DT_CREATE_COLUMN_NAME = "users.dt_create";

    private static final String ALIAS_USERS_DT_CREATE_COLUMN_NAME = "u_dt_create";

    private static final String USERS_DT_UPDATE_COLUMN_NAME = "users.dt_update";

    private static final String ALIAS_USERS_DT_UPDATE_COLUMN_NAME = "u_dt_update";

    private static final String FAIL_RECEIVE_SINGLE_SUPPLY_MESSAGE = "Ошибка получения услуги";

    private static final String FAIL_RECEIVE_LIST_SUPPLIES_MESSAGE = "Ошибка получения списка услуг";

    private static final String FAIL_CREATE_SUPPLY_MESSAGE = "Ошибка сохранения данных в базу";

    private static final String FAIL_UPDATE_SUPPLY_MESSAGE = "Ошибка обновления данных";

    private static final String FAIL_DELETE_SUPPLY_MESSAGE = "Ошибка удаления данных";

    private static final String IMPOSSIBLE_GET_SUPPLY_CAUSE_NULL = "Невозможно получить услугу так как в качестве аргументы был передан null";

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
             PreparedStatement selectOnePs = c.prepareStatement(createGetOneByUuidSqlStatement())) {

            selectOnePs.setObject(1, uuid);

            ResultSet rs = selectOnePs.executeQuery();

            Supply supply = createSupply(rs);

            rs.close();

            return Optional.ofNullable(supply);

        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_SINGLE_SUPPLY_MESSAGE)));
        }

    }

    @Override
    public List<Supply> get() {

        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectAllPs = c.prepareStatement(createGetAllSqlStatement())) {

            ResultSet rs = selectAllPs.executeQuery();

            List<Supply> listOfSupplies = createListOfSupplies(rs);

            rs.close();

            return listOfSupplies;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_SUPPLIES_MESSAGE)));
        }
    }


    @Override
    public Supply save(Supply supply) {
        NullCheckUtil.checkNull(IMPOSSIBLE_SAVE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement insertSupplyPs = c.prepareStatement(createInsertSqlStatement());
             PreparedStatement insertSupplyUsersPs = c.prepareStatement(createInsertSupplyUsersSqlStatement())) {
            c.setAutoCommit(false);

            insertSupply(supply, insertSupplyPs);

            insertSupplyUsers(supply, insertSupplyUsersPs);

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
             PreparedStatement updateSupplyPs = c.prepareStatement(createUpdateSqlStatement());
             PreparedStatement deleteSupplyUsersPs = c.prepareStatement(createDeleteSupplyUsersSqlStatement());
             PreparedStatement insertSupplyUsersPs = c.prepareStatement(createInsertSupplyUsersSqlStatement())) {

            c.setAutoCommit(false);

            deleteSupplyUsers(supply, deleteSupplyUsersPs);

            insertSupplyUsers(supply, insertSupplyUsersPs);

            Supply updatedSupply = updateSupply(supply, updateSupplyPs);

            c.commit();

            return updatedSupply;
        } catch (SQLException e) {
            throw new UpdatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
        }
    }

    @Override
    public void delete(Supply supply) {
        NullCheckUtil.checkNull(IMPOSSIBLE_DELETE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement deleteSupplyPs = c.prepareStatement(createDeleteSqlStatement());
             PreparedStatement deleteSupplyUsersPs = c.prepareStatement(createDeleteSupplyUsersSqlStatement())) {
            c.setAutoCommit(false);

            deleteSupplyUsers(supply, deleteSupplyUsersPs);

            deleteSupply(supply, deleteSupplyPs);

            c.commit();
        } catch (SQLException e) {
            throw new DeletingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_SUPPLY_MESSAGE)));
        }
    }

    private void deleteSupply(Supply supply, PreparedStatement deleteSupplyPs) throws SQLException {
        deleteSupplyPs.setObject(1, supply.getUuid());
        deleteSupplyPs.setObject(2, supply.getDtUpdate());

        int supplyExecuteUpdate = deleteSupplyPs.executeUpdate();
        if (supplyExecuteUpdate < 1) {
            throw new DeletingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_SUPPLY_MESSAGE)));
        }
    }

    private void insertSupply(Supply supply, PreparedStatement insertSupplyPs) throws SQLException {
        UUID supplyUuid = supply.getUuid();
        insertSupplyPs.setObject(1, supplyUuid);
        insertSupplyPs.setString(2, supply.getName());
        insertSupplyPs.setBigDecimal(3, supply.getPrice());
        Integer duration = supply.getDuration();
        if (duration != null) {
            insertSupplyPs.setInt(4, supply.getDuration());
        } else {
            insertSupplyPs.setNull(4, Types.NULL);
        }
        insertSupplyPs.setObject(5, supply.getDtCreate());
        insertSupplyPs.setObject(6, supply.getDtUpdate());


        insertSupplyPs.execute();
    }

    private void insertSupplyUsers(Supply supply, PreparedStatement insertUserSuppliesPs) throws SQLException {
        insertUserSuppliesPs.setObject(1, supply.getUuid());
        List<User> masters = supply.getMasters();
        for (User master : masters) {
            insertUserSuppliesPs.setObject(2, master.getUuid());
            insertUserSuppliesPs.addBatch();
        }
        insertUserSuppliesPs.executeBatch();
    }

    private void deleteSupplyUsers(Supply supply, PreparedStatement deleteUserSuppliesPs) throws SQLException {
        deleteUserSuppliesPs.setObject(1, supply.getUuid());
        deleteUserSuppliesPs.execute();
    }

    private Supply updateSupply(Supply supply, PreparedStatement updateSupplyPs) throws SQLException {
        Supply updatedSupply = null;

        updateSupplyPs.setString(1, supply.getName());
        updateSupplyPs.setBigDecimal(2, supply.getPrice());
        Integer duration = supply.getDuration();
        if (duration != null) {
            updateSupplyPs.setInt(3, supply.getDuration());
        } else {
            updateSupplyPs.setNull(3, Types.NULL);
        }
        updateSupplyPs.setObject(4, supply.getUuid());
        updateSupplyPs.setObject(5, supply.getDtUpdate());

        ResultSet rs = updateSupplyPs.executeQuery();
        if (rs.next()) {
            updatedSupply = createSupplyWithoutMasters(rs);
        }

        if (updatedSupply == null) {
            throw new UpdatingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
        }

        updatedSupply.setMasters(supply.getMasters());

        return updatedSupply;
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

        sb.append(SUPPLY_UUID_COLUMN_NAME);
        sb.append(", ");
        sb.append(SUPPLY_NAME_COLUMN_NAME);
        sb.append(", ");
        sb.append(PRICE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DURATION_COLUMN_NAME);
        sb.append(", ");
        sb.append(SUPPLY_DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(SUPPLY_DT_UPDATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);
        sb.append(", ");
        sb.append(USERS_NAME_COLUMN_NAME);
        sb.append(" AS ");
        sb.append(ALIAS_USERS_NAME_COLUMN_NAME);
        sb.append(", ");
        sb.append(PHONE_NUMBER_COLUMN_NAME);
        sb.append(", ");
        sb.append(USER_ROLE_COLUMN_NAME);
        sb.append(", ");
        sb.append(USERS_DT_CREATE_COLUMN_NAME);
        sb.append(" AS ");
        sb.append(ALIAS_USERS_DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(USERS_DT_UPDATE_COLUMN_NAME);
        sb.append(" AS ");
        sb.append(ALIAS_USERS_DT_UPDATE_COLUMN_NAME);
        sb.append(" FROM ");
        sb.append(SUPPLY_TABLE_NAME);
        sb.append(" LEFT JOIN ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(" ON ");
        sb.append(SUPPLY_UUID_COLUMN_NAME);
        sb.append(" = ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(".");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append(" LEFT JOIN ");
        sb.append(USERS_TABLE_NAME);
        sb.append(" ON ");
        sb.append(USERS_UUID_COLUMN_NAME);
        sb.append(" = ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(".");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);

        return sb.toString();
    }

    private String createGetOneByUuidSqlStatement() {
        StringBuilder sb = new StringBuilder(createGetAllSqlStatement());
        sb.append(" WHERE ");
        sb.append(SUPPLY_UUID_COLUMN_NAME);
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

    private String createInsertSupplyUsersSqlStatement() {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append("(");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append(", ");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);
        sb.append(") VALUES (?, ?)");
        return sb.toString();
    }

    private String createDeleteSupplyUsersSqlStatement() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append("=?");
        return sb.toString();
    }


    private Supply createSupplyWithoutMasters(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);
        String name = rs.getString(NAME_COLUMN_NAME);
        BigDecimal price = rs.getBigDecimal(PRICE_COLUMN_NAME);
        Integer duration = (Integer) rs.getObject(DURATION_COLUMN_NAME);
        LocalDateTime dtCreate = rs.getTimestamp(DT_CREATE_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtUpdate = rs.getTimestamp(DT_UPDATE_COLUMN_NAME).toLocalDateTime();
        return new Supply(uuid, name, price, duration, new ArrayList<>(), dtCreate, dtUpdate);
    }

    private Supply createSupply(ResultSet rs) throws SQLException {
        Supply supply = null;
        while (rs.next()) {
            if (supply == null) {
                supply = createSupplyWithoutMasters(rs);
            }
            supply.getMasters().add(createUser(rs));
        }
        return supply;
    }

    private List<Supply> createListOfSupplies(ResultSet rs) throws SQLException {
        Map<UUID, Supply> uuidSupplyMap = new HashMap<>();
        while (rs.next()) {
            UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);

            Supply supply = uuidSupplyMap.getOrDefault(uuid, createSupplyWithoutMasters(rs));

            UUID masterUuid = (UUID) rs.getObject(USERS_SUPPLY_USER_COLUMN_NAME);
            if (masterUuid != null) {
                supply.getMasters().add(createUser(rs));
            }

            uuidSupplyMap.put(uuid, supply);
        }
        return uuidSupplyMap.values().stream().toList();
    }

    private User createUser(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject(USERS_SUPPLY_USER_COLUMN_NAME);
        String name = rs.getString(ALIAS_USERS_NAME_COLUMN_NAME);
        String phone = rs.getString(PHONE_NUMBER_COLUMN_NAME);
        UserRole role = UserRole.fromString(rs.getString(USER_ROLE_COLUMN_NAME));
        LocalDateTime dtCreate = rs.getTimestamp(ALIAS_USERS_DT_CREATE_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtUpdate = rs.getTimestamp(ALIAS_USERS_DT_UPDATE_COLUMN_NAME).toLocalDateTime();

        return new User(uuid, name, phone, role, new ArrayList<>(), dtCreate, dtUpdate);
    }

}
