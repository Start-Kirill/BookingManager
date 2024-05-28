package org.example.endpoints.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.example.service.api.IUserService;

@WebServlet("/user")
public class UserServlet extends HttpServlet {

    private final IUserService userService;

    public UserServlet(IUserService userService) {
        this.userService = userService;
    }
}
