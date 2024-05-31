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
import org.example.service.api.IUserService;
import org.example.service.factory.UserServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(urlPatterns = {"/user", "/user/*"})
public class UserServlet extends HttpServlet {

    private final IUserService userService;

    private final ObjectMapper objectMapper;

    public UserServlet() {
        this.userService = UserServiceFactory.getInstance();
        this.objectMapper = ObjectMapperFactory.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = this.userService.get();
        List<UserDto> userDtos = users.stream().map(UserMapper.INSTANCE::userToUserDto).toList();
        PrintWriter writer = resp.getWriter();
        writer.write(this.objectMapper.writeValueAsString(userDtos));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        UserCreateDto userCreateDto = this.objectMapper.readValue(inputStream, UserCreateDto.class);
        User user = this.userService.save(userCreateDto);
        resp.getWriter().write(this.objectMapper.writeValueAsString(UserMapper.INSTANCE.userToUserDto(user)));
    }
}
