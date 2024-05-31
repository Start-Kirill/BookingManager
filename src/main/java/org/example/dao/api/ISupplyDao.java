package org.example.dao.api;

import org.example.core.entity.Supply;

import java.util.List;
import java.util.UUID;

public interface ISupplyDao extends ICRUDDao<Supply> {

    List<Supply> get(List<UUID> uuids);
}
