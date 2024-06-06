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

    private static final String IMPOSSIBLE_CHECK_IF_EXISTS_SUPPLY_CAUSE_NULL = "Невозможно проверить существование услуги так как в качестве аргумента был передан null";

    private static final String FAIL_CHECK_IF_SUPPLY_EXISTS_MESSAGE = "Ошибка проверки существования услуги";

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
    public List<Supply> get(List<UUID> uuids) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_LIST_OF_SUPPLIES_CAUSE_NULL, uuids);
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectInUuidsPs = c.prepareStatement(createGetAccordingToUuidsSqlStatement(uuids.size()))) {

            for (int i = 0; i < uuids.size(); i++) {
                selectInUuidsPs.setObject(i + 1, uuids.get(i));
            }

            ResultSet rs = selectInUuidsPs.executeQuery();

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
             PreparedStatement selectOneMastersFreePs = c.prepareStatement(createGetOneByUuidWithoutMastersSqlStatement())) {

            selectOneMastersFreePs.setObject(1, uuid);

            ResultSet rs = selectOneMastersFreePs.executeQuery();
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
             PreparedStatement insertSupplyPs = c.prepareStatement(createInsertSqlStatement());
             PreparedStatement insertSupplyUsersPs = c.prepareStatement(createInsertSupplyUsersSqlStatement())) {
            c.setAutoCommit(false);

            insertSupply(supply, insertSupplyPs);

            insertSupplyUsers(supply, insertSupplyUsersPs);

            updateMaters(supply.getMasters());

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
             PreparedStatement insertSupplyUsersPs = c.prepareStatement(createInsertSupplyUsersSqlStatement());
             PreparedStatement selectSupplyUsersPs = c.prepareStatement(createGetSupplyUsersSqlStatement())) {

            c.setAutoCommit(false);

            List<User> performedMasters = findPerformedMasters(supply, selectSupplyUsersPs);

            deleteSupplyUsers(supply, deleteSupplyUsersPs);

            insertSupplyUsers(supply, insertSupplyUsersPs);

            Supply updatedSupply = updateSupply(supply, updateSupplyPs);

            updateMaters(performedMasters);

            c.commit();

            return updatedSupply;
        } catch (SQLException e) {
            throw new UpdatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
        }
    }

    @Override
    public void systemUpdate(Supply supply) throws SQLException {
        NullCheckUtil.checkNull(IMPOSSIBLE_UPDATE_SUPPLY_CAUSE_NULL, supply);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement systemSupplyUpdatePs = c.prepareStatement(createSystemUpdateSqlStatement())) {
            c.setAutoCommit(false);

            systemSupplyUpdatePs.setObject(1, supply.getUuid());
            systemSupplyUpdatePs.setObject(2, supply.getDtUpdate());

            if (systemSupplyUpdatePs.executeUpdate() < 1) {
                throw new UpdatingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_SUPPLY_MESSAGE)));
            }

            c.commit();
        }
    }

    @Override
    public boolean exists(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_CHECK_IF_EXISTS_SUPPLY_CAUSE_NULL, uuid);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement existsPs = c.prepareStatement(createExistsSqlStatement())) {
            existsPs.setObject(1, uuid);
            return existsPs.execute();
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_CHECK_IF_SUPPLY_EXISTS_MESSAGE)));
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

            updateMaters(supply.getMasters());

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

    private void updateMaters(List<User> masters) throws SQLException {
        for (User master : masters) {
            UserDaoFactory.getInstance().systemUpdate(master);
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

    private List<User> getSupplyMasters(Supply supply, PreparedStatement selectUserSuppliesPs) throws SQLException {
        List<User> performedUser = new ArrayList<>();

        selectUserSuppliesPs.setObject(1, supply.getUuid());
        ResultSet rs = selectUserSuppliesPs.executeQuery();
        while (rs.next()) {
            UUID uuid = rs.getObject(USERS_SUPPLY_USER_COLUMN_NAME, UUID.class);
            performedUser.add(UserDaoFactory.getInstance().get(uuid).orElseThrow());
        }
        rs.close();
        return performedUser;
    }

    private List<User> findPerformedMasters(Supply supply, PreparedStatement selectUserSuppliesPs) throws SQLException {
        List<User> performedUser = getSupplyMasters(supply, selectUserSuppliesPs);
        supply.getMasters().forEach(m -> {
            if (!performedUser.contains(m)) {
                performedUser.add(m);
            }
        });
        return performedUser;
    }

    private String createExistsSqlStatement() {
        StringBuilder sb = new StringBuilder("SELECT EXISTS ( SELECT 1 FROM ");
        sb.append(SUPPLY_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ?)");
        return sb.toString();
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

    private String createGetSupplyUsersSqlStatement() {
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
