package org.example.service.factory;

import org.example.dao.factory.SupplyDaoFactory;
import org.example.service.SupplyService;
import org.example.service.api.ISupplyService;

public class SupplyServiceFactory {

    private static volatile ISupplyService instance;

    private SupplyServiceFactory() {
    }

    public static ISupplyService getInstance() {
        if (instance == null) {
            synchronized (SupplyServiceFactory.class) {
                if (instance == null) {
                    instance = new SupplyService(SupplyDaoFactory.getInstance());
                }
            }
        }
        return instance;
    }
}
