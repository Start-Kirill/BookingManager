package org.example.dao;

import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.dao.api.ISupplyDao;
import org.example.dao.api.IUserDao;
import org.example.dao.exceptions.CreatingDBDataException;
import org.example.dao.exceptions.ReceivingDBDataException;
import org.example.dao.factory.ds.DataBaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private final ISupplyDao supplyDao;

    public UserDao(ISupplyDao supplyDao) {
        this.supplyDao = supplyDao;
    }

    @Override
    public Optional<User> get(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public List<User> get() {

        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(createGetAllSqlStatement())) {
            c.setAutoCommit(false);

            ResultSet rs = ps.executeQuery();

            List<User> listOfUsers = createListOfUsers(rs);

            rs.close();

            return listOfUsers;

        } catch (SQLException e) {
            throw new ReceivingDBDataException(FAIL_RECEIVE_LIST_USERS_MESSAGE, e.getCause());
        }
    }


    @Override
    public User save(User user) {
        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps1 = c.prepareStatement(createInsertUserSqlStatement());
             PreparedStatement ps2 = c.prepareStatement(createInsertUserSuppliesSqlStatement())) {
            c.setAutoCommit(false);

            UUID userUuid = user.getUuid();

            ps1.setObject(1, userUuid);
            ps1.setString(2, user.getName());
            ps1.setString(3, user.getPhoneNumber());
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
            throw new CreatingDBDataException(FAIL_CREATE_USER_MESSAGE, e.getCause());
        }
    }


    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(User user) {

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

    private List<User> createListOfUsers(ResultSet rs) throws SQLException {
        Map<UUID, User> uuidUserMap = new HashMap<>();
        while (rs.next()) {
            UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);

            User user = uuidUserMap.getOrDefault(uuid, createUser(rs));

            Object rawUuid = rs.getObject(USERS_SUPPLY_SUPPLY_COLUMN_NAME);
            if (rawUuid != null) {
                UUID supplyUuid = (UUID) rawUuid;
                user.getSupplies().add(this.supplyDao.get(supplyUuid).orElseThrow());
            }

            uuidUserMap.put(uuid, user);
        }
        return uuidUserMap.values().stream().toList();
    }

    private User createUser(ResultSet rs) throws SQLException {
        UUID uuid = (UUID) rs.getObject(UUID_COLUMN_NAME);
        String name = rs.getString(NAME_COLUMN_NAME);
        String phone = rs.getString(PHONE_NUMBER_COLUMN_NAME);
        UserRole role = UserRole.fromString(rs.getString(USER_ROLE_COLUMN_NAME));
        LocalDateTime dtCreate = rs.getTimestamp(DT_CREATE_COLUMN_NAME).toLocalDateTime();
        LocalDateTime dtUpdate = rs.getTimestamp(DT_UPDATE_COLUMN_NAME).toLocalDateTime();

        return new User(uuid, name, phone, role, new ArrayList<>(), dtCreate, dtUpdate);
    }
}
