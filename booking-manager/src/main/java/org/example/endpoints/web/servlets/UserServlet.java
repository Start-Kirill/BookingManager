package org.example.endpoints.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.UserCreateDto;
import org.example.core.dto.UserDto;
import org.example.core.entity.User;
import org.example.core.mappers.UserMapper;
import org.example.endpoints.web.factory.ObjectMapperFactory;
import org.example.endpoints.web.util.PathVariablesSearcherUtil;
import org.example.service.api.IUserService;
import org.example.service.factory.UserServiceFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = {"/user", "/user/*", "/user/*/dt_update/"})
public class UserServlet extends HttpServlet {

    private static final String URL_PART_BEFORE_UUID_NAME = "user";

    private static final String URL_PART_BEFORE_DT_UPDATE_NAME = "dt_update";

    private final IUserService userService;

    private final ObjectMapper objectMapper;

    public UserServlet() {
        this.userService = UserServiceFactory.getInstance();
        this.objectMapper = ObjectMapperFactory.getInstance();
    }

    public UserServlet(IUserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
        if (uuid == null) {
            List<User> users = this.userService.get();
            List<UserDto> userDtos = users.stream().map(UserMapper.INSTANCE::userToUserDto).toList();
            resp.getWriter().write(this.objectMapper.writeValueAsString(userDtos));
        } else {
            User user = this.userService.get(uuid);
            UserDto userDto = UserMapper.INSTANCE.userToUserDto(user);
            resp.getWriter().write(this.objectMapper.writeValueAsString(userDto));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        UserCreateDto userCreateDto = this.objectMapper.readValue(inputStream, UserCreateDto.class);
        User user = this.userService.save(userCreateDto);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(this.objectMapper.writeValueAsString(UserMapper.INSTANCE.userToUserDto(user)));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
        LocalDateTime dtUpdate = PathVariablesSearcherUtil.retrieveDtUpdateAsPathVariables(req, URL_PART_BEFORE_DT_UPDATE_NAME);
        ServletInputStream inputStream = req.getInputStream();
        UserCreateDto userCreateDto = this.objectMapper.readValue(inputStream, UserCreateDto.class);
        User user = this.userService.update(userCreateDto, uuid, dtUpdate);
        resp.getWriter().write(this.objectMapper.writeValueAsString(UserMapper.INSTANCE.userToUserDto(user)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
        LocalDateTime dtUpdate = PathVariablesSearcherUtil.retrieveDtUpdateAsPathVariables(req, URL_PART_BEFORE_DT_UPDATE_NAME);
        this.userService.delete(uuid, dtUpdate);
    }
}
