package org.example.service.factory;

import org.example.service.api.IUserService;

public class UserServiceFactory {

    private static volatile IUserService instance;

    private UserServiceFactory() {
    }

    public static IUserService getInstance() {
        if (instance == null) {
            synchronized (UserServiceFactory.class) {
                if (instance == null) {
//                    TODO create user service
                }
            }
        }
        return instance;
    }
}
