package org.example.service;

import org.example.core.dto.SupplyCreateDto;
import org.example.core.entity.Supply;
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
    public Supply get(UUID uuid) {
        return null;
    }

    @Override
    public List<Supply> get() {
        return null;
    }

    @Override
    public Supply save(SupplyCreateDto supplyCreateDto) {
        Supply supply = new Supply();
        supply.setUuid(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        supply.setDtCreate(now);
        supply.setDtUpdate(now);

        supply.setName(supplyCreateDto.getName());
        supply.setPrice(supplyCreateDto.getPrice());
        supply.setDuration(supplyCreateDto.getDuration());

        return this.supplyDao.save(supply);
    }

    @Override
    public Supply update(SupplyCreateDto supplyCreateDto, UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }

    @Override
    public Supply delete(UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }
}
