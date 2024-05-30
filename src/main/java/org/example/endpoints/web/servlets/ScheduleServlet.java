package org.example.endpoints.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.example.endpoints.web.factory.ObjectMapperFactory;
import org.example.service.api.IScheduleService;
import org.example.service.factory.ScheduleServiceFactory;

@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {

    private final IScheduleService scheduleService;

    private final ObjectMapper objectMapper;

    public ScheduleServlet() {
        this.scheduleService = ScheduleServiceFactory.getInstance();
        this.objectMapper = ObjectMapperFactory.getInstance();
    }
}