package org.example.service;

import org.example.core.dto.SupplyCreateDto;
import org.example.core.entity.Supply;
import org.example.core.mappers.SupplyMapper;
import org.example.dao.api.ISupplyDao;
import org.example.service.api.ISupplyService;
import org.example.service.exceptions.InvalidDurationException;
import org.example.service.exceptions.ObjectNotUpToDatedException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class SupplyService implements ISupplyService {

    private static final Integer MAX_DURATION = 720;

    private static final String SUPPLY_NOT_UP_TO_DATED_MESSAGE = "Объект не актуален. Получите новый объект и попробуйте снова";

    private static final String DURATION_TOO_LONG_MESSAGE = "Длительность услуги не может превышать " + MAX_DURATION + "минут";

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
    public List<Supply> get(List<UUID> uuids) {
        return this.supplyDao.get(uuids);
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
            throw new ObjectNotUpToDatedException(SUPPLY_NOT_UP_TO_DATED_MESSAGE);
        }

        actualSupply.setName(supplyCreateDto.getName());
        actualSupply.setDuration(supplyCreateDto.getDuration());
        actualSupply.setPrice(supplyCreateDto.getPrice());

        return this.supplyDao.update(actualSupply);
    }

    @Override
    public void delete(UUID uuid, LocalDateTime dtUpdate) {
        Supply actualSupply = this.get(uuid);
        LocalDateTime actualDtUpdate = actualSupply.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(SUPPLY_NOT_UP_TO_DATED_MESSAGE);
        }
        this.supplyDao.delete(actualSupply);
    }

    private void validate(SupplyCreateDto supplyCreateDto) {
        Integer duration = supplyCreateDto.getDuration();
        if (duration > MAX_DURATION) {
            throw new InvalidDurationException(DURATION_TOO_LONG_MESSAGE);
        }

    }
}
