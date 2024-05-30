package org.example.dao;

import org.example.core.entity.Supply;
import org.example.dao.api.ISupplyDao;
import org.example.dao.factory.ds.DataBaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SupplyDao implements ISupplyDao {
    @Override
    public Optional<Supply> get(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public List<Supply> get() {
        return null;
    }

    @Override
    public Supply save(Supply supply) {

        try (Connection c = DataBaseConnectionFactory.getConnection();
             PreparedStatement ps1 = c.prepareStatement("INSERT INTO app.supply(" +
                     "uuid, name, price, duration, dt_create, dt_update)" +
                     "VALUES (?, ?, ?, ?, ?, ?)")) {
            c.setAutoCommit(false);

            ps1.setObject(1, supply.getUuid());
            ps1.setString(2, supply.getName());
            ps1.setBigDecimal(3, supply.getPrice());
            ps1.setInt(4, supply.getDuration());
            ps1.setObject(5, supply.getDtCreate());
            ps1.setObject(6, supply.getDtUpdate());

            ps1.execute();

            c.commit();
        } catch (SQLException e) {
//            TODO
            throw new RuntimeException(e);
        }

        return supply;
    }

    @Override
    public Supply update(Supply supply) {
        return null;
    }

    @Override
    public void delete(Supply supply) {

    }
}
