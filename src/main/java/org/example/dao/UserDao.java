package org.example.dao;

import org.example.core.entity.User;
import org.example.dao.api.IUserDao;
import org.example.dao.factory.ds.DataBaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDao implements IUserDao {

    private static final String UUID_FILED_NAME = "uuid";

    private static final String NAME_FILED_NAME = "name";

    private static final String PHONE_NUMBER_FILED_NAME = "phone_number";

    private static final String USER_ROLE_FILED_NAME = "user_role";

    private static final String DT_CREATE_FILED_NAME = "dt_create";

    private static final String DT_UPDATE_FILED_NAME = "dt_update";

    @Override
    public Optional<User> get(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public List<User> get() {

//        try (Connection connection = DataBaseConnectionFactory.getConnection();
//             PreparedStatement ps = connection.prepareStatement("SELECT * FROM app.user ")) {
//
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//
//            }
//
//            rs.close();
//
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(User user) {

    }
}
