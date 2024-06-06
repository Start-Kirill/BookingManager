package org.example.dao.api;

import org.example.core.entity.Supply;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISupplyDao extends ICRUDDao<Supply> {

    List<Supply> get(List<UUID> uuids);

    Optional<Supply> getWithoutMasters(UUID uuid);

    void systemUpdate(Supply supply) throws SQLException;
}
