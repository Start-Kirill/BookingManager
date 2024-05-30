package org.example.service.factory;

import org.example.dao.factory.ScheduleDaoFactory;
import org.example.service.ScheduleService;
import org.example.service.api.IScheduleService;

public class ScheduleServiceFactory {

    private static volatile IScheduleService instance;

    private ScheduleServiceFactory() {
    }

    public static IScheduleService getInstance() {
        if (instance == null) {
            synchronized (ScheduleServiceFactory.class) {
                if (instance == null) {
                    instance = new ScheduleService(ScheduleDaoFactory.getInstance());
                }
            }
        }
        return instance;
    }
}
