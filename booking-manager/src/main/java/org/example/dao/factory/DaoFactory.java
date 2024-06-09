package org.example.dao.factory;

import org.example.core.entity.Schedule;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.DaoType;
import org.example.dao.ScheduleDao;
import org.example.dao.SupplyDao;
import org.example.dao.UserDao;
import org.example.dao.api.ICRUDDao;
import org.example.dao.factory.ds.DataBaseConnectionFactory;

public class DaoFactory {

    private static volatile ICRUDDao<User> userDao;

    private static volatile ICRUDDao<Supply> supplyDao;

    private static volatile ICRUDDao<Schedule> scheduleDao;


    public static ICRUDDao getInstance(DaoType type) {
        return switch (type) {
            case USER -> getUserDao();
            case SUPPLY -> getSupplyDao();
            case SCHEDULE -> getScheduleDao();
        };
    }

    private static ICRUDDao<User> getUserDao() {
        if (userDao == null) {
            synchronized (DaoFactory.class) {
                if (userDao == null) {
                    userDao = new UserDao(DataBaseConnectionFactory.getInstance());
                }
            }
        }
        return userDao;
    }

    private static ICRUDDao<Supply> getSupplyDao() {
        if (supplyDao == null) {
            synchronized (DaoFactory.class) {
                if (supplyDao == null) {
                    supplyDao = new SupplyDao(DataBaseConnectionFactory.getInstance());
                }
            }
        }
        return supplyDao;
    }

    private static ICRUDDao<Schedule> getScheduleDao() {
        if (scheduleDao == null) {
            synchronized (DaoFactory.class) {
                if (scheduleDao == null) {
                    scheduleDao = new ScheduleDao(getUserDao(), DataBaseConnectionFactory.getInstance());
                }
            }
        }
        return scheduleDao;
    }
}
