package org.example.dao.factory;

import org.example.dao.SupplyDao;
import org.example.dao.api.ISupplyDao;

public class SupplyDaoFactory {

    private static volatile ISupplyDao instance;

    private SupplyDaoFactory() {
    }

    public static ISupplyDao getInstance() {
        if (instance == null) {
            synchronized (SupplyDaoFactory.class) {
                if (instance == null) {
                    instance = new SupplyDao();
                }
            }
        }
        return instance;
    }
}
