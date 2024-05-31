package org.example.service;

import org.example.core.dto.UserCreateDto;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.mappers.UserMapper;
import org.example.dao.api.IUserDao;
import org.example.service.api.ISupplyService;
import org.example.service.api.IUserService;
import org.example.service.exceptions.ObjectNotUpToDatedException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService implements IUserService {

    private static final String USER_NOT_UP_TO_DATED_MESSAGE = "Пользователь не актуален. Получите актуального пользователя и попробуйте снова";

    private final IUserDao userDao;

    private final ISupplyService supplyService;

    public UserService(IUserDao userDao,
                       ISupplyService supplyService) {
        this.userDao = userDao;
        this.supplyService = supplyService;
    }

    @Override
    public User get(UUID uuid) {
        return this.userDao.get(uuid).orElseThrow();
    }

    @Override
    public List<User> get() {
        return this.userDao.get();
    }

    @Override
    public User save(UserCreateDto userCreateDto) {
        validate(userCreateDto);

        User user = UserMapper.INSTANCE.userCreateDtoToUser(userCreateDto);

        user.setUuid(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        user.setDtCreate(now);
        user.setDtUpdate(now);

        List<UUID> suppliesUuid = userCreateDto.getSupplies();
        List<Supply> supplies = new ArrayList<>();
        if (suppliesUuid != null) {
            supplies = this.supplyService.get(suppliesUuid);
        }
        user.setSupplies(supplies);

        return this.userDao.save(user);
    }


    @Override
    public User update(UserCreateDto userCreateDto, UUID uuid, LocalDateTime dtUpdate) {
        validate(userCreateDto);

        User actualUser = this.get(uuid);
        LocalDateTime actualDtUpdate = actualUser.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(USER_NOT_UP_TO_DATED_MESSAGE);
        }

        actualUser.setName(userCreateDto.getName());
        actualUser.setPhoneNumber(userCreateDto.getPhoneNumber());
        actualUser.setUserRole(userCreateDto.getUserRole());
        List<UUID> supplyUuids = userCreateDto.getSupplies();

        List<Supply> supplies = new ArrayList<>();
        if (supplyUuids != null) {
            supplies = this.supplyService.get(supplyUuids);
        }
        actualUser.setSupplies(supplies);

        return this.userDao.update(actualUser);
    }

    @Override
    public void delete(UUID uuid, LocalDateTime dtUpdate) {

    }

    //    TODO
    private void validate(UserCreateDto userCreateDto) {
    }


}
