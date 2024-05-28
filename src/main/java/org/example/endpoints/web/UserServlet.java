package org.example.endpoints.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.example.service.api.IUserService;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private final IUserService userService;

    private final ObjectMapper objectMapper;

    public UserServlet(IUserService userService,
                       ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }
}
