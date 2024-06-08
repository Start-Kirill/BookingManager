package org.example.service.factory;

import org.example.dao.factory.UserDaoFactory;
import org.example.service.UserService;
import org.example.service.api.IUserService;

public class UserServiceFactory {

    private static volatile IUserService instance;

    private UserServiceFactory() {
    }

    public static IUserService getInstance() {
        if (instance == null) {
            synchronized (UserServiceFactory.class) {
                if (instance == null) {
                    instance = new UserService(UserDaoFactory.getInstance(),
                            SupplyServiceFactory.getInstance());
                }
            }
        }
        return instance;
    }
}
