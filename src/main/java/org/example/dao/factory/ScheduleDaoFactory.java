package org.example.dao.factory;

import org.example.dao.ScheduleDao;
import org.example.dao.api.IScheduleDao;
import org.example.dao.factory.ds.DataBaseConnectionFactory;

public class ScheduleDaoFactory {

    private static volatile IScheduleDao instance;

    private ScheduleDaoFactory() {
    }

    public static IScheduleDao getInstance() {
        if (instance == null) {
            synchronized (ScheduleDaoFactory.class) {
                if (instance == null) {
                    instance = new ScheduleDao(UserDaoFactory.getInstance(),
                            DataBaseConnectionFactory.getInstance());
                }
            }
        }
        return instance;
    }
}
