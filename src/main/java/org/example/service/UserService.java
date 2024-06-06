package org.example.service;

import org.example.core.dto.UserCreateDto;
import org.example.core.dto.errors.ErrorResponse;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.ErrorType;
import org.example.core.enums.UserRole;
import org.example.core.mappers.UserMapper;
import org.example.core.util.NullCheckUtil;
import org.example.dao.api.IUserDao;
import org.example.service.api.ISupplyService;
import org.example.service.api.IUserService;
import org.example.service.exceptions.InvalidUserBodyException;
import org.example.service.exceptions.ObjectNotUpToDatedException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService implements IUserService {

    private static final String REG_EX_TO_CHECK_PHONE_NUMBER = "\\+\\d{3,15}";

    private static final String PHONE_NUMBER_FIELD_NAME = "phone_number";

    private static final String NAME_FIELD_NAME = "name";

    private static final String USER_ROLE_FIELD_NAME = "role";

    private static final String USER_ROLE_CAN_NOT_BE_NULL_MESSAGE = "Поле role должно быть заполнено";

    private static final String NAME_CAN_NOT_BE_NULL_MESSAGE = "Поле name должно быть заполнено";

    private static final String USER_NOT_UP_TO_DATED_MESSAGE = "Пользователь не актуален. Получите актуального пользователя и попробуйте снова";

    private static final String INVALID_PHONE_NUMBER_MESSAGE = "Не верный формат номера телефона. Номер телефона должен быть в формате +[код страны][номер]. Повторите, пожалуйста, ввод";

    private static final String IMPOSSIBLE_GET_USER_CAUSE_NULL = "Невозможно получить пользователя так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_GET_LIST_OF_USERS_CAUSE_NULL = "Невозможно получить список пользователей так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_SAVE_USER_CAUSE_NULL = "Невозможно создать пользователя так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_UPDATE_USER_CAUSE_NULL = "Невозможно обновить пользователя так как в качестве аргумента был передан null";

    private static final String IMPOSSIBLE_DELETE_USER_CAUSE_NULL = "Невозможно удалить пользователя так как в качестве аргумента был передан null";

    private final IUserDao userDao;

    private final ISupplyService supplyService;

    public UserService(IUserDao userDao,
                       ISupplyService supplyService) {
        this.userDao = userDao;
        this.supplyService = supplyService;
    }

    @Override
    public User get(UUID uuid) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_USER_CAUSE_NULL, uuid);
        return this.userDao.get(uuid).orElseThrow();
    }

    @Override
    public List<User> get() {
        return this.userDao.get();
    }

    @Override
    public List<User> get(List<UUID> uuids) {
        NullCheckUtil.checkNull(IMPOSSIBLE_GET_LIST_OF_USERS_CAUSE_NULL, uuids);
        return this.userDao.get(uuids);
    }

    @Override
    public boolean exists(UUID uuid) {
        return this.userDao.exists(uuid);
    }

    @Override
    public User save(UserCreateDto userCreateDto) {
        NullCheckUtil.checkNull(IMPOSSIBLE_SAVE_USER_CAUSE_NULL, userCreateDto);
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
        NullCheckUtil.checkNull(IMPOSSIBLE_UPDATE_USER_CAUSE_NULL, userCreateDto, uuid, dtUpdate);
        validate(userCreateDto);

        User actualUser = this.get(uuid);
        LocalDateTime actualDtUpdate = actualUser.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(List.of(new ErrorResponse(ErrorType.ERROR, USER_NOT_UP_TO_DATED_MESSAGE)));
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
        NullCheckUtil.checkNull(IMPOSSIBLE_DELETE_USER_CAUSE_NULL, uuid, dtUpdate);
        User actualUser = this.get(uuid);
        LocalDateTime actualDtUpdate = actualUser.getDtUpdate().truncatedTo(ChronoUnit.MILLIS);
        if (!actualDtUpdate.equals(dtUpdate.truncatedTo(ChronoUnit.MILLIS))) {
            throw new ObjectNotUpToDatedException(List.of(new ErrorResponse(ErrorType.ERROR, USER_NOT_UP_TO_DATED_MESSAGE)));
        }
        this.userDao.delete(actualUser);
    }

    private void validate(UserCreateDto userCreateDto) {
        Map<String, String> errors = new HashMap<>();

        String name = userCreateDto.getName();
        if (name == null) {
            errors.put(NAME_FIELD_NAME, NAME_CAN_NOT_BE_NULL_MESSAGE);
        }

        UserRole userRole = userCreateDto.getUserRole();
        if (userRole == null) {
            errors.put(USER_ROLE_FIELD_NAME, USER_ROLE_CAN_NOT_BE_NULL_MESSAGE);
        }

        String phoneNumber = userCreateDto.getPhoneNumber();
        if (phoneNumber != null && !validatePhoneNumber(userCreateDto.getPhoneNumber())) {
            errors.put(PHONE_NUMBER_FIELD_NAME, INVALID_PHONE_NUMBER_MESSAGE);
        }
        if (!errors.isEmpty()) {
            throw new InvalidUserBodyException(errors);
        }
    }

    private boolean validatePhoneNumber(String phone) {
        if (phone != null) {
            Pattern pattern = Pattern.compile(REG_EX_TO_CHECK_PHONE_NUMBER);
            Matcher matcher = pattern.matcher(phone);
            return matcher.matches();
        }
        return false;
    }
}
