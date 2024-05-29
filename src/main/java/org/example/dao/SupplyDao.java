package org.example.dao;

import org.example.core.entity.Supply;
import org.example.dao.api.ISupplyDao;

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
        return null;
    }

    @Override
    public Supply update(Supply supply) {
        return null;
    }

    @Override
    public void delete(Supply supply) {

    }
}
