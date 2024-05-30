package org.example.dao.factory;

import org.example.dao.UserDao;
import org.example.dao.api.IUserDao;

public class UserDaoFactory {

    private static volatile IUserDao instance;

    private UserDaoFactory() {
    }

    public static IUserDao getInstance() {
        if (instance == null) {
            synchronized (UserDaoFactory.class) {
                if (instance == null) {
                    instance = new UserDao();
                }
            }
        }
        return instance;
    }
}
