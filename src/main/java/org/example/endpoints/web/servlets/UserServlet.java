package org.example.endpoints.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.endpoints.web.factory.ObjectMapperFactory;
import org.example.service.api.IUserService;
import org.example.service.factory.UserServiceFactory;

import java.io.IOException;

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
//        List<UserDto> userDtos = this.userService.get();
//        PrintWriter writer = resp.getWriter();
//        writer.write(this.objectMapper.writeValueAsString(userDtos));
    }
}
