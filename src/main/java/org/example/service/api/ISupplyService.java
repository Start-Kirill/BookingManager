package org.example.service.api;

import org.example.core.dto.SupplyCreateDto;
import org.example.core.entity.Supply;

import java.util.List;
import java.util.UUID;

public interface ISupplyService extends ICRUDService<Supply, SupplyCreateDto> {

    List<Supply> get(List<UUID> uuids);

    boolean exists(UUID uuid);
}
