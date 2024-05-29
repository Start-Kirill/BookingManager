package org.example.dao.factory;

import org.example.dao.ScheduleDao;
import org.example.dao.api.IScheduleDao;

public class ScheduleDaoFactory {

    private static volatile IScheduleDao instance;

    private ScheduleDaoFactory() {
    }

    public static IScheduleDao getInstance() {
        if (instance == null) {
            synchronized (ScheduleDaoFactory.class) {
                if (instance == null) {
                    instance = new ScheduleDao();
                }
            }
        }
        return instance;
    }
}
