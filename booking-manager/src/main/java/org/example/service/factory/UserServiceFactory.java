package org.example.service.factory;

import org.example.core.enums.DaoType;
import org.example.dao.factory.DaoFactory;
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
                    instance = new UserService(DaoFactory.getInstance(DaoType.USER),
                            SupplyServiceFactory.getInstance());
                }
            }
        }
        return instance;
    }
}
