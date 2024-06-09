package org.example.service.factory;

import org.example.core.enums.DaoType;
import org.example.dao.factory.DaoFactory;
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
                    instance = new SupplyService(DaoFactory.getInstance(DaoType.SUPPLY));
                }
            }
        }
        return instance;
    }
}
