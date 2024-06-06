package org.example.dao;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.ErrorType;
import org.example.core.util.NullCheckUtil;
import org.example.dao.api.IDataBaseConnection;
import org.example.dao.api.ISupplyDao;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.ReceivingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;
import org.example.dao.factory.UserDaoFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class SupplyDao implements ISupplyDao {

    private static final String USERS_SUPPLY_TABLE_NAME = "app.users_supply";

    private static final String USERS_SUPPLY_USER_COLUMN_NAME = "user_uuid";

    private static final String USERS_SUPPLY_SUPPLY_COLUMN_NAME = "supply_uuid";

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

            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();

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
             PreparedStatement ps = c.prepareStatement(createGetAllSqlStatement())) {

            ResultSet rs = ps.executeQuery();

            List<Supply> listOfSupplies = createListOfSupplies(rs);

            rs.close();

            return listOfSupplies;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_SUPPLIES_MESSAGE)));
        }
    }

    @Override
    public List<Supply> get(List<UUID> uuids) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_LIST_OF_SUPPLIES_CAUSE_NULL, uuids);
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetAccordingToUuidsSqlStatement(uuids.size()))) {

            for (int i = 0; i < uuids.size(); i++) {
                ps.setObject(i + 1, uuids.get(i));
            }

            ResultSet rs = ps.executeQuery();

            List<Supply> listOfSupplies = createListOfSupplies(rs);

            rs.close();

            return listOfSupplies;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_SUPPLIES_MESSAGE)));
        }
    }

    @Override
    public Optional<Supply> getWithoutMasters(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_SUPPLY_CAUSE_NULL, uuid);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetOneByUuidWithoutMastersSqlStatement())) {

            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            Supply supply = null;
            if (rs.next()) {
                supply = createSupplyWithoutMasters(rs);
            }

            rs.close();

            return Optional.ofNullable(supply);

        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_SINGLE_SUPPLY_MESSAGE)));
        }
    }


    @Override
    public Supply save(Supply supply) {
        NullCheckUtil.checkNull(IMPOSSIBLE_SAVE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps1 = c.prepareStatement(createInsertSqlStatement());
             PreparedStatement ps2 = c.prepareStatement(createInsertUserSuppliesSqlStatement())) {
            c.setAutoCommit(false);

            UUID supplyUuid = supply.getUuid();
            ps1.setObject(1, supplyUuid);
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

            ps2.setObject(1, supplyUuid);
            List<User> masters = supply.getMasters();
            for (User master : masters) {
                ps2.setObject(2, master.getUuid());
                ps2.addBatch();
            }

            ps1.execute();
            ps2.executeBatch();

            for (User master : masters) {
                UserDaoFactory.getInstance().systemUpdate(master);
            }

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
             PreparedStatement ps = c.prepareStatement(createUpdateSqlStatement());
             PreparedStatement ps2 = c.prepareStatement(createDeleteUserSuppliesSqlStatement());
             PreparedStatement ps3 = c.prepareStatement(createInsertUserSuppliesSqlStatement());
             PreparedStatement ps4 = c.prepareStatement(createGetUserSuppliesSqlStatement())) {

            c.setAutoCommit(false);

            ps4.setObject(1, supply.getUuid());
            ResultSet resultSet = ps4.executeQuery();
            List<User> performedUser = new ArrayList<>();
            while (resultSet.next()) {
                UUID uuid = resultSet.getObject(USERS_SUPPLY_USER_COLUMN_NAME, UUID.class);
                performedUser.add(UserDaoFactory.getInstance().get(uuid).orElseThrow());
            }

            Supply updatedSupply = null;

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
            if (rs.next()) {
                updatedSupply = createSupplyWithoutMasters(rs);
            }

            if (updatedSupply == null) {
                throw new UpdatingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
            }
            updatedSupply.setMasters(supply.getMasters());

            ps2.setObject(1, supply.getUuid());

            ps3.setObject(1, supply.getUuid());
            for (User u : supply.getMasters()) {
                if (!performedUser.contains(u)) {
                    performedUser.add(u);
                }
                ps3.setObject(2, u.getUuid());
                ps3.addBatch();
            }

            ps2.execute();
            ps3.executeBatch();

            for (User user : performedUser) {
                UserDaoFactory.getInstance().systemUpdate(user);
            }


            c.commit();
            rs.close();

            return updatedSupply;
        } catch (SQLException e) {
            throw new UpdatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
        }
    }

    @Override
    public void systemUpdate(Supply supply) throws SQLException {
        NullCheckUtil.checkNull(IMPOSSIBLE_UPDATE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createSystemUpdateSqlStatement())) {
            c.setAutoCommit(false);

            ps.setObject(1, supply.getUuid());
            ps.setObject(2, supply.getDtUpdate());

            if (ps.executeUpdate() < 1) {
                throw new UpdatingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
            }

            c.commit();
        }
    }

    @Override
    public void delete(Supply supply) {
        NullCheckUtil.checkNull(IMPOSSIBLE_DELETE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createDeleteSqlStatement());
             PreparedStatement ps2 = c.prepareStatement(createDeleteUserSuppliesSqlStatement())) {
            c.setAutoCommit(false);

            ps.setObject(1, supply.getUuid());
            ps.setObject(2, supply.getDtUpdate());

            ps2.setObject(1, supply.getUuid());

            int userSupplyExecuteUpdate = ps2.executeUpdate();
            int supplyExecuteUpdate = ps.executeUpdate();
            if (userSupplyExecuteUpdate < 1 || supplyExecuteUpdate < 1) {
                throw new DeletingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_SUPPLY_MESSAGE)));
            }

            for (User user : supply.getMasters()) {
                UserDaoFactory.getInstance().systemUpdate(user);
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
        sb.append(", ");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);
        sb.append(" FROM ");
        sb.append(SUPPLY_TABLE_NAME);
        sb.append(" LEFT JOIN ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(" ON ");
        sb.append(SUPPLY_TABLE_NAME);
        sb.append(".");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(".");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);

        return sb.toString();
    }

    private String createGetAllWithoutMastersSqlStatement() {
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

    private String createGetOneByUuidWithoutMastersSqlStatement() {
        StringBuilder sb = new StringBuilder(createGetAllWithoutMastersSqlStatement());
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ?");
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

    private String createSystemUpdateSqlStatement() {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(SUPPLY_TABLE_NAME);
        sb.append(" SET ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = NOW()");
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ? AND ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = ?");
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

    private String createInsertUserSuppliesSqlStatement() {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append("(");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append(", ");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);
        sb.append(") VALUES (?, ?)");
        return sb.toString();
    }

    private String createDeleteUserSuppliesSqlStatement() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append("=?");
        return sb.toString();
    }

    private String createGetUserSuppliesSqlStatement() {
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);
        sb.append(" FROM ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append(" =?");
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
        List<UUID> masterUuids = new ArrayList<>();
        while (rs.next()) {
            if (supply == null) {
                supply = createSupplyWithoutMasters(rs);
            }
            Object rawMasterUuid = rs.getObject(USERS_SUPPLY_USER_COLUMN_NAME);
            if (rawMasterUuid != null) {
                UUID masterUuid = (UUID) rawMasterUuid;
                masterUuids.add(masterUuid);
            }
        }
        if (supply != null) {
            supply.setMasters(UserDaoFactory.getInstance().getWithoutSupplies(masterUuids));
        }
        return supply;
    }

    private List<Supply> createListOfSupplies(ResultSet rs) throws SQLException {
        Map<UUID, Supply> uuidSupplyMap = new HashMap<>();
        while (rs.next()) {
            UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);

            Supply supply = uuidSupplyMap.getOrDefault(uuid, createSupplyWithoutMasters(rs));

            Object rawUuid = rs.getObject(USERS_SUPPLY_USER_COLUMN_NAME);
            if (rawUuid != null) {
                UUID masterUuid = (UUID) rawUuid;
                supply.getMasters().add(UserDaoFactory.getInstance().getWithoutSupplies(masterUuid).orElseThrow());
            }

            uuidSupplyMap.put(uuid, supply);
        }
        return uuidSupplyMap.values().stream().toList();
    }

}
