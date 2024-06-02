package org.example.service;

import org.example.core.dto.UserCreateDto;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.mappers.UserMapper;
import org.example.dao.api.IUserDao;
import org.example.service.api.ISupplyService;
import org.example.service.api.IUserService;
import org.example.service.exceptions.ObjectNotUpToDatedException;
import org.example.service.exceptions.PhoneNumberNotCorrectException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService implements IUserService {

    private static final String REG_EX_TO_CHECK_PHONE_NUMBER = "\\+\\d{3,15}";

    private static final String USER_NOT_UP_TO_DATED_MESSAGE = "Пользователь не актуален. Получите актуального пользователя и попробуйте снова";

    private static final String INVALID_PHONE_NUMBER_MESSAGE = "Не верный формат номера телефона. Номер телефона должен быть в формате +[код страны][номер]. Повторите, пожалуйста, ввод";

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
        User actualUser = this.get(uuid);
        LocalDateTime actualDtUpdate = actualUser.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(USER_NOT_UP_TO_DATED_MESSAGE);
        }
        this.userDao.delete(actualUser);
    }

    private void validate(UserCreateDto userCreateDto) {
        validatePhoneNumber(userCreateDto.getPhoneNumber());
    }

    private void validatePhoneNumber(String phone) {
        if (phone != null) {
            Pattern pattern = Pattern.compile(REG_EX_TO_CHECK_PHONE_NUMBER);
            Matcher matcher = pattern.matcher(phone);
            if (!matcher.matches()) {
                throw new PhoneNumberNotCorrectException(INVALID_PHONE_NUMBER_MESSAGE);
            }
        }
    }


}
