package org.example.dao;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.ErrorType;
import org.example.core.enums.UserRole;
import org.example.core.util.NullCheckUtil;
import org.example.dao.api.IDataBaseConnection;
import org.example.dao.api.ISupplyDao;
import org.example.dao.api.IUserDao;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.DeletingDBDataException;
import org.example.dao.exceptions.ReceivingDBDataException;
import org.example.dao.exceptions.UpdatingDBDataException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;


public class UserDao implements IUserDao {

    private static final String USER_TABLE_NAME = "app.users";

    private static final String USERS_SUPPLY_TABLE_NAME = "app.users_supply";

    private static final String UUID_COLUMN_NAME = "uuid";

    private static final String NAME_COLUMN_NAME = "name";

    private static final String PHONE_NUMBER_COLUMN_NAME = "phone_number";

    private static final String USER_ROLE_COLUMN_NAME = "role";

    private static final String DT_CREATE_COLUMN_NAME = "dt_create";

    private static final String DT_UPDATE_COLUMN_NAME = "dt_update";

    private static final String USERS_SUPPLY_USER_COLUMN_NAME = "user_uuid";

    private static final String USERS_SUPPLY_SUPPLY_COLUMN_NAME = "supply_uuid";

    private static final String FAIL_CREATE_USER_MESSAGE = "Ошибка сохранения пользователя";

    private static final String FAIL_RECEIVE_LIST_USERS_MESSAGE = "Ошибка получения пользователей";

    private static final String FAIL_RECEIVE_USER_MESSAGE = "Ошибка получения пользователя";

    private static final String FAIL_UPDATE_USER_MESSAGE = "Ошибка обновления пользователя";

    private static final String FAIL_DELETE_USER_MESSAGE = "Ошибка удаления пользователя";

    private static final String IMPOSSIBLE_GET_USER_CAUSE_NULL = "Невозможно получить пользователя так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_GET_LIST_OF_USERS_CAUSE_NULL = "Невозможно получить список пользователей так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_SAVE_USER_CAUSE_NULL = "Невозможно создать пользователя так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_UPDATE_USER_CAUSE_NULL = "Невозможно обновить пользователя так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_DELETE_USER_CAUSE_NULL = "Невозможно удалить пользователя так как в качестве аргумента был передан null";


    private final ISupplyDao supplyDao;

    private final IDataBaseConnection dataBaseConnection;

    public UserDao(ISupplyDao supplyDao,
                   IDataBaseConnection dataBaseConnection) {
        this.supplyDao = supplyDao;
        this.dataBaseConnection = dataBaseConnection;
    }

    @Override
    public Optional<User> get(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_USER_CAUSE_NULL, uuid);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectOnePs = c.prepareStatement(createGetOneByUuidSqlStatement())) {

            selectOnePs.setObject(1, uuid);

            ResultSet rs = selectOnePs.executeQuery();

            User user = createUser(rs);

            rs.close();

            return Optional.ofNullable(user);

        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_USER_MESSAGE)));
        }
    }

    @Override
    public List<User> get(List<UUID> uuids) {
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_LIST_OF_USERS_CAUSE_NULL, uuids);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectInUuidsPs = c.prepareStatement(createGetAccordingToUuidsSqlStatement(uuids.size()))) {

            for (int i = 0; i < uuids.size(); i++) {
                selectInUuidsPs.setObject(i + 1, uuids.get(i));
            }

            ResultSet rs = selectInUuidsPs.executeQuery();

            List<User> listOfUsers = createListOfUsers(rs);

            rs.close();

            return listOfUsers;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_USERS_MESSAGE)));
        }
    }

    @Override
    public List<User> getWithoutSupplies(List<UUID> uuids) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_LIST_OF_USERS_CAUSE_NULL, uuids);
        if (uuids.isEmpty()) {
            return new ArrayList<>();
        }
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectInUuidsSuppliesFreePs = c.prepareStatement(createGetAccordingToUuidsWithoutSuppliesSqlStatement(uuids.size()))) {

            for (int i = 0; i < uuids.size(); i++) {
                selectInUuidsSuppliesFreePs.setObject(i + 1, uuids.get(i));
            }

            ResultSet rs = selectInUuidsSuppliesFreePs.executeQuery();

            List<User> listOfUsers = new ArrayList<>();

            while (rs.next()) {
                listOfUsers.add(createUserWithoutSupplies(rs));
            }

            rs.close();

            return listOfUsers;
        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_USERS_MESSAGE)));
        }
    }

    @Override
    public Optional<User> getWithoutSupplies(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_USER_CAUSE_NULL, uuid);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectOneSuppliesFreePs = c.prepareStatement(createGetOneByUuidWithoutSuppliesSqlStatement())) {

            selectOneSuppliesFreePs.setObject(1, uuid);

            ResultSet rs = selectOneSuppliesFreePs.executeQuery();
            User user = null;
            if (rs.next()) {
                user = createUserWithoutSupplies(rs);
            }

            rs.close();

            return Optional.ofNullable(user);

        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_USER_MESSAGE)));
        }
    }

    @Override
    public List<User> get() {

        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement selectAllPs = c.prepareStatement(createGetAllSqlStatement())) {

            ResultSet rs = selectAllPs.executeQuery();

            List<User> listOfUsers = createListOfUsers(rs);

            rs.close();

            return listOfUsers;

        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_LIST_USERS_MESSAGE)));
        }
    }


    @Override
    public User save(User user) {
        NullCheckUtil.checkNull(IMPOSSIBLE_SAVE_USER_CAUSE_NULL, user);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement insertUserPs = c.prepareStatement(createInsertUserSqlStatement());
             PreparedStatement insertsUserSuppliesPS = c.prepareStatement(createInsertUserSuppliesSqlStatement())) {
            c.setAutoCommit(false);

            insertUser(user, insertUserPs);

            insertUserSupplies(user, insertsUserSuppliesPS);

            updateSupplies(user.getSupplies());

            c.commit();

            return user;
        } catch (SQLException e) {
            throw new CreatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_CREATE_USER_MESSAGE)));
        }
    }


    @Override
    public User update(User user) {
        NullCheckUtil.checkNull(IMPOSSIBLE_UPDATE_USER_CAUSE_NULL, user);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement updateUserPs = c.prepareStatement(createUpdateSqlStatement());
             PreparedStatement deleteUserSuppliesPs = c.prepareStatement(createDeleteUserSuppliesSqlStatement());
             PreparedStatement insertUserSuppliesPs = c.prepareStatement(createInsertUserSuppliesSqlStatement());
             PreparedStatement selectUserSuppliesPs = c.prepareStatement(createGetUserSuppliesSqlStatement())) {
            c.setAutoCommit(false);

            List<Supply> performedSupplies = findPerformedSupplies(user, selectUserSuppliesPs);

            User updatedUser = updatedUser(user, updateUserPs);

            deleteUserSupplies(user, deleteUserSuppliesPs);

            insertUserSupplies(user, insertUserSuppliesPs);

            updateSupplies(performedSupplies);

            c.commit();

            return updatedUser;
        } catch (SQLException e) {
            throw new UpdatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_USER_MESSAGE)));
        }
    }


    @Override
    public void systemUpdate(User user) throws SQLException {
        NullCheckUtil.checkNull(IMPOSSIBLE_UPDATE_USER_CAUSE_NULL, user);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement systemUpdateUserPs = c.prepareStatement(createSystemUpdateSqlStatement())) {
            c.setAutoCommit(false);

            systemUpdateUserPs.setObject(1, user.getUuid());
            systemUpdateUserPs.setObject(2, user.getDtUpdate());

            if (systemUpdateUserPs.executeUpdate() < 1) {
                throw new UpdatingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_USER_MESSAGE)));
            }

            c.commit();
        }
    }

    @Override
    public void delete(User user) {
        NullCheckUtil.checkNull(IMPOSSIBLE_DELETE_USER_CAUSE_NULL, user);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement deleteUserSuppliesPs = c.prepareStatement(createDeleteUserSuppliesSqlStatement());
             PreparedStatement deleteUserPs = c.prepareStatement(createDeleteUserSqlStatement())) {

            c.setAutoCommit(false);

            deleteUserSupplies(user, deleteUserSuppliesPs);

            deleteUser(user, deleteUserPs);

            updateSupplies(user.getSupplies());

            c.commit();
        } catch (SQLException e) {
            throw new DeletingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_USER_MESSAGE)));
        }
    }

    private void deleteUser(User user, PreparedStatement deleteUserPs) throws SQLException {
        deleteUserPs.setObject(1, user.getUuid());
        deleteUserPs.setObject(2, user.getDtUpdate());
        int userExecuteUpdate = deleteUserPs.executeUpdate();
        if (userExecuteUpdate < 1) {
            throw new DeletingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_USER_MESSAGE)));
        }
    }

    private void updateSupplies(List<Supply> user) throws SQLException {
        for (Supply supply : user) {
            this.supplyDao.systemUpdate(supply);
        }
    }

    private void insertUser(User user, PreparedStatement insertUserPs) throws SQLException {
        UUID userUuid = user.getUuid();

        insertUserPs.setObject(1, userUuid);
        insertUserPs.setString(2, user.getName());
        String phoneNumber = user.getPhoneNumber();
        if (phoneNumber == null) {
            insertUserPs.setNull(3, Types.NULL);
        } else {
            insertUserPs.setString(3, phoneNumber);
        }
        String userRole = user.getUserRole() == null ? null : user.getUserRole().toString();
        insertUserPs.setString(4, userRole);
        insertUserPs.setObject(5, user.getDtCreate());
        insertUserPs.setObject(6, user.getDtUpdate());
        insertUserPs.execute();
    }

    private void insertUserSupplies(User user, PreparedStatement insertsUserSuppliesPS) throws SQLException {
        List<Supply> supplies = user.getSupplies();
        insertsUserSuppliesPS.setObject(1, user.getUuid());
        for (Supply s : supplies) {
            insertsUserSuppliesPS.setObject(2, s.getUuid());
            insertsUserSuppliesPS.addBatch();
        }

        insertsUserSuppliesPS.executeBatch();
    }

    private void deleteUserSupplies(User user, PreparedStatement deleteUserSuppliesPs) throws SQLException {
        deleteUserSuppliesPs.setObject(1, user.getUuid());
        deleteUserSuppliesPs.execute();
    }

    private User updatedUser(User user, PreparedStatement updateUserPs) throws SQLException {
        updateUserPs.setString(1, user.getName());
        String phoneNumber = user.getPhoneNumber();
        if (phoneNumber == null) {
            updateUserPs.setNull(2, Types.NULL);
        } else {
            updateUserPs.setString(2, phoneNumber);
        }
        String userRole = user.getUserRole() == null ? null : user.getUserRole().toString();
        updateUserPs.setString(3, userRole);
        updateUserPs.setObject(4, user.getUuid());
        updateUserPs.setObject(5, user.getDtUpdate());

        ResultSet rs = updateUserPs.executeQuery();

        User updatedUser = null;
        if (rs.next()) {
            updatedUser = createUserWithoutSupplies(rs);
            updatedUser.setSupplies(user.getSupplies());

        }
        if (updatedUser == null) {
            throw new UpdatingDBDataException(List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_USER_MESSAGE)));
        }

        rs.close();
        return updatedUser;
    }

    private List<Supply> findPerformedSupplies(User user, PreparedStatement selectUserSuppliesPs) throws SQLException {
        List<Supply> performedSupplies = new ArrayList<>();
        selectUserSuppliesPs.setObject(1, user.getUuid());
        ResultSet rs = selectUserSuppliesPs.executeQuery();
        while (rs.next()) {
            UUID uuid = (UUID) rs.getObject(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
            performedSupplies.add(this.supplyDao.get(uuid).orElseThrow());
        }
        for (Supply s : user.getSupplies()) {
            if (!performedSupplies.contains(s)) {
                performedSupplies.add(s);
            }
        }
        rs.close();
        return performedSupplies;
    }


    private String createInsertUserSqlStatement() {
        StringBuilder sb = new StringBuilder("INSERT INTO app.users(");
        sb.append(UUID_COLUMN_NAME);
        sb.append(", ");
        sb.append(NAME_COLUMN_NAME);
        sb.append(", ");
        sb.append(PHONE_NUMBER_COLUMN_NAME);
        sb.append(", ");
        sb.append(USER_ROLE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(") VALUES (?, ?, ?, ?, ?, ?)");
        return sb.toString();
    }

    private String createInsertUserSuppliesSqlStatement() {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append("(");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);
        sb.append(", ");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append(") VALUES (?, ?)");
        return sb.toString();
    }

    private String createGetUserSuppliesSqlStatement() {
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append(" FROM ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);
        sb.append(" =?");
        return sb.toString();
    }

    private String createGetAllSqlStatement() {
        StringBuilder sb = new StringBuilder("SELECT ");

        sb.append(UUID_COLUMN_NAME);
        sb.append(", ");
        sb.append(NAME_COLUMN_NAME);
        sb.append(", ");
        sb.append(PHONE_NUMBER_COLUMN_NAME);
        sb.append(", ");
        sb.append(USER_ROLE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
        sb.append(" FROM ");
        sb.append(USER_TABLE_NAME);
        sb.append(" LEFT JOIN ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(" ON ");
        sb.append(USER_TABLE_NAME);
        sb.append(".");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(".");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);


        return sb.toString();
    }

    private String createGetAllWithoutSuppliesSqlStatement() {
        StringBuilder sb = new StringBuilder("SELECT ");

        sb.append(UUID_COLUMN_NAME);
        sb.append(", ");
        sb.append(NAME_COLUMN_NAME);
        sb.append(", ");
        sb.append(PHONE_NUMBER_COLUMN_NAME);
        sb.append(", ");
        sb.append(USER_ROLE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_CREATE_COLUMN_NAME);
        sb.append(", ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" FROM ");
        sb.append(USER_TABLE_NAME);

        return sb.toString();
    }

    private String createGetAccordingToUuidsWithoutSuppliesSqlStatement(int number) {
        StringBuilder sb = new StringBuilder(createGetAllWithoutSuppliesSqlStatement());
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


    private String createUpdateSqlStatement() {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(USER_TABLE_NAME);
        sb.append(" SET ");
        sb.append(NAME_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(PHONE_NUMBER_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(USER_ROLE_COLUMN_NAME);
        sb.append(" = ?,");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = NOW()");
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ? AND ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = ?");
        sb.append(" RETURNING *");
        return sb.toString();
    }

    private String createSystemUpdateSqlStatement() {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(USER_TABLE_NAME);
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

    private String createDeleteUserSuppliesSqlStatement() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(USERS_SUPPLY_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(USERS_SUPPLY_USER_COLUMN_NAME);
        sb.append("=?");
        return sb.toString();
    }

    private String createDeleteUserSqlStatement() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(USER_TABLE_NAME);
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ? AND ");
        sb.append(DT_UPDATE_COLUMN_NAME);
        sb.append(" = ?");
        return sb.toString();
    }

    private String createGetOneByUuidSqlStatement() {
        StringBuilder sb = new StringBuilder(createGetAllSqlStatement());
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ?");
        return sb.toString();
    }

    private String createGetOneByUuidWithoutSuppliesSqlStatement() {
        StringBuilder sb = new StringBuilder(createGetAllWithoutSuppliesSqlStatement());
        sb.append(" WHERE ");
        sb.append(UUID_COLUMN_NAME);
        sb.append(" = ?");
        return sb.toString();
    }

    private List<User> createListOfUsers(ResultSet rs) throws SQLException {
        Map<UUID, User> uuidUserMap = new HashMap<>();
        while (rs.next()) {
            UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);

            User user = uuidUserMap.getOrDefault(uuid, createUserWithoutSupplies(rs));

            Object rawUuid = rs.getObject(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
            if (rawUuid != null) {
                UUID supplyUuid = (UUID) rawUuid;
                user.getSupplies().add(this.supplyDao.getWithoutMasters(supplyUuid).orElseThrow());
            }

            uuidUserMap.put(uuid, user);
        }
        return uuidUserMap.values().stream().toList();
    }

    private User createUserWithoutSupplies(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);
        String name = rs.getString(NAME_COLUMN_NAME);
        String phone = rs.getString(PHONE_NUMBER_COLUMN_NAME);
        UserRole role = UserRole.fromString(rs.getString(USER_ROLE_COLUMN_NAME));
        LocalDateTime dtCreate = rs.getTimestamp(DT_CREATE_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtUpdate = rs.getTimestamp(DT_UPDATE_COLUMN_NAME).toLocalDateTime();

        return new User(uuid, name, phone, role, new ArrayList<>(), dtCreate, dtUpdate);
    }

    private User createUser(ResultSet rs) throws SQLException {
        User user = null;
        List<UUID> supplyUuids = new ArrayList<>();
        while (rs.next()) {
            if (user == null) {
                user = createUserWithoutSupplies(rs);
            }
            Object rawSupplyUuid = rs.getObject(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
            if (rawSupplyUuid != null) {
                UUID supplyUuid = (UUID) rawSupplyUuid;
                supplyUuids.add(supplyUuid);
                user.getSupplies().add(this.supplyDao.get(supplyUuid).orElseThrow());
            }
        }
        if (user != null) {
            user.setSupplies(this.supplyDao.get(supplyUuids));
        }
        return user;
    }

}
