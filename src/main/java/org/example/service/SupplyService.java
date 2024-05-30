package org.example.service;

import org.example.core.dto.SupplyCreateDto;
import org.example.core.entity.Supply;
import org.example.core.mappers.SupplyMapper;
import org.example.dao.api.ISupplyDao;
import org.example.service.api.ISupplyService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class SupplyService implements ISupplyService {

    private final ISupplyDao supplyDao;

    public SupplyService(ISupplyDao supplyDao) {
        this.supplyDao = supplyDao;
    }


    @Override
    public Supply get(UUID uuid) {
        return this.supplyDao.get(uuid).orElseThrow();
    }

    @Override
    public List<Supply> get() {
        return this.supplyDao.get();
    }

    @Override
    public Supply save(SupplyCreateDto supplyCreateDto) {

        validate(supplyCreateDto);

        Supply supply = SupplyMapper.INSTANCE.supplyCreateDtoToSupply(supplyCreateDto);

        supply.setUuid(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        supply.setDtCreate(now);
        supply.setDtUpdate(now);

        return this.supplyDao.save(supply);
    }


    @Override
    public Supply update(SupplyCreateDto supplyCreateDto, UUID uuid, LocalDateTime dtUpdate) {
        validate(supplyCreateDto);

        Supply actualSupply = this.get(uuid);
        LocalDateTime actualDtUpdate = actualSupply.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
//            TODO
            throw new SecurityException("Объект не актуален. Получите новый объект и попробуйте снова");
        }

        actualSupply.setName(supplyCreateDto.getName());
        actualSupply.setDuration(supplyCreateDto.getDuration());
        actualSupply.setPrice(supplyCreateDto.getPrice());

        return this.supplyDao.update(actualSupply);
    }

    @Override
    public Supply delete(UUID uuid, LocalDateTime dtUpdate) {
        return null;
    }

    //    TODO
    private void validate(SupplyCreateDto supplyCreateDto) {

    }
}
