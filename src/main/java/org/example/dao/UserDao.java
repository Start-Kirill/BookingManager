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
             PreparedStatement ps = c.prepareStatement(createGetOneByUuidSqlStatement())) {

            ps.setObject(1, uuid);

            ResultSet rs = ps.executeQuery();
            User user = createUser(rs);

            rs.close();

            return Optional.ofNullable(user);

        } catch (SQLException e) {
            throw new ReceivingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_RECEIVE_USER_MESSAGE)));
        }
    }

    @Override
    public List<User> get() {

        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetAllSqlStatement())) {

            ResultSet rs = ps.executeQuery();

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
             PreparedStatement ps1 = c.prepareStatement(createInsertUserSqlStatement());
             PreparedStatement ps2 = c.prepareStatement(createInsertUserSuppliesSqlStatement())) {
            c.setAutoCommit(false);

            UUID userUuid = user.getUuid();

            ps1.setObject(1, userUuid);
            ps1.setString(2, user.getName());
            String phoneNumber = user.getPhoneNumber();
            if (phoneNumber == null) {
                ps1.setNull(3, Types.NULL);
            } else {
                ps1.setString(3, phoneNumber);
            }
            ps1.setString(4, user.getUserRole().toString());
            ps1.setObject(5, user.getDtCreate());
            ps1.setObject(6, user.getDtUpdate());

            List<Supply> supplies = user.getSupplies();
            ps2.setObject(1, userUuid);
            for (Supply s : supplies) {
                ps2.setObject(2, s.getUuid());
                ps2.addBatch();
            }

            ps1.execute();
            ps2.executeBatch();

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
             PreparedStatement ps1 = c.prepareStatement(createUpdateSqlStatement());
             PreparedStatement ps2 = c.prepareStatement(createDeleteUserSuppliesSqlStatement());
             PreparedStatement ps3 = c.prepareStatement(createInsertUserSuppliesSqlStatement())) {

            c.setAutoCommit(false);

            ps1.setString(1, user.getName());
            ps1.setString(2, user.getPhoneNumber());
            String phoneNumber = user.getPhoneNumber();
            if (phoneNumber == null) {
                ps1.setNull(3, Types.NULL);
            } else {
                ps1.setString(3, phoneNumber);
            }
            ps1.setObject(4, user.getUuid());
            ps1.setObject(5, user.getDtUpdate());

            ps2.setObject(1, user.getUuid());

            List<Supply> supplies = user.getSupplies();
            ps3.setObject(1, user.getUuid());
            for (Supply s : supplies) {
                ps3.setObject(2, s.getUuid());
                ps3.addBatch();
            }

            ps2.execute();
            ps3.executeBatch();

            ResultSet rs = ps1.executeQuery();

            User updatedUser = null;
            if (rs.next()) {
                updatedUser = createUserWithoutSupplies(rs);
                updatedUser.setSupplies(supplies);

            }

            c.commit();
            rs.close();

            return updatedUser;
        } catch (SQLException e) {
            throw new UpdatingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_UPDATE_USER_MESSAGE)));
        }
    }

    @Override
    public void delete(User user) {
        NullCheckUtil.checkNull(IMPOSSIBLE_DELETE_USER_CAUSE_NULL, user);
        try (Connection c = dataBaseConnection.getConnection();
             PreparedStatement ps1 = c.prepareStatement(createDeleteUserSuppliesSqlStatement());
             PreparedStatement ps2 = c.prepareStatement(createDeleteUserSqlStatement())) {

            c.setAutoCommit(false);

            ps1.setObject(1, user.getUuid());

            ps2.setObject(1, user.getUuid());
            ps2.setObject(2, user.getDtUpdate());

            ps1.execute();
            ps2.execute();

            c.commit();
        } catch (SQLException e) {
            throw new DeletingDBDataException(e.getCause(), List.of(new ErrorResponse(ErrorType.ERROR, FAIL_DELETE_USER_MESSAGE)));
        }
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

    private List<User> createListOfUsers(ResultSet rs) throws SQLException {
        Map<UUID, User> uuidUserMap = new HashMap<>();
        while (rs.next()) {
            UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);

            User user = uuidUserMap.getOrDefault(uuid, createUserWithoutSupplies(rs));

            Object rawUuid = rs.getObject(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
            if (rawUuid != null) {
                UUID supplyUuid = (UUID) rawUuid;
                user.getSupplies().add(this.supplyDao.get(supplyUuid).orElseThrow());
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
        while (rs.next()) {
            if (user == null) {
                user = createUserWithoutSupplies(rs);
            }
            Object rawSupplyUuid = rs.getObject(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
            if (rawSupplyUuid != null) {
                UUID supplyUuid = (UUID) rawSupplyUuid;
                user.getSupplies().add(this.supplyDao.get(supplyUuid).orElseThrow());
            }
        }
        return user;
    }

}
