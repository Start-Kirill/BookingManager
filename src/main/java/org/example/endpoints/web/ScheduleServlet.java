package org.example.endpoints.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.example.service.api.IScheduleService;

@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {

    private final IScheduleService scheduleService;

    public ScheduleServlet(IScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }
}
