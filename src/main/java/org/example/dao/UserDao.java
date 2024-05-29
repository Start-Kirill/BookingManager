package org.example.dao;

import org.example.core.entity.User;
import org.example.dao.api.IUserDao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDao implements IUserDao {
    @Override
    public Optional<User> get(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public List<User> get() {
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
