package org.example.service;

import org.example.core.dto.SupplyDto;
import org.example.core.dto.SupplyCreateDto;
import org.example.dao.api.ISupplyDao;
import org.example.service.api.ISupplyService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SupplyService implements ISupplyService {

    private final ISupplyDao supplyDao;

    public SupplyService(ISupplyDao supplyDao) {
        this.supplyDao = supplyDao;
    }

    @Override
    public SupplyCreateDto get(UUID uuid) {
        return null;
    }

    @Override
    public List<SupplyCreateDto> get() {
        return null;
    }

    @Override
    public SupplyCreateDto save(SupplyDto supplyDto) {
        return null;
    }

    @Override
    public SupplyCreateDto update(SupplyDto supplyDto, UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }

    @Override
    public SupplyCreateDto delete(UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }
}
