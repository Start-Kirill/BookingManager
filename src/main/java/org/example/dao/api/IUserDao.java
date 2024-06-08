package org.example.dao.api;

import org.example.core.entity.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserDao extends ICRUDDao<User> {

    List<User> get(List<UUID> uuids);

    List<User> getWithoutSupplies(List<UUID> uuids);

    Optional<User> getWithoutSupplies(UUID uuid);

    void systemUpdate(User user) throws SQLException;

    boolean exists(UUID uuid);
}
