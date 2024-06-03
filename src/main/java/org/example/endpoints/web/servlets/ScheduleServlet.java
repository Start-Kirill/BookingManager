package org.example.endpoints.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.ScheduleCreateDto;
import org.example.core.dto.ScheduleDto;
import org.example.core.entity.Schedule;
import org.example.core.mappers.ScheduleMapper;
import org.example.endpoints.web.factory.ObjectMapperFactory;
import org.example.endpoints.web.util.PathVariablesSearcherUtil;
import org.example.service.api.IScheduleService;
import org.example.service.factory.ScheduleServiceFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = {"/schedule", "/schedule/*", "/schedule/*/dt_update/*"})
public class ScheduleServlet extends HttpServlet {

    private static final String URL_PART_BEFORE_UUID_NAME = "schedule";

    private static final String URL_PART_BEFORE_DT_UPDATE_NAME = "dt_update";

    private static final String COORDINATES_ABSENT_OR_WRONG_MESSAGE = "Координаты отсутствуют или неверны";

    private final IScheduleService scheduleService;

    private final ObjectMapper objectMapper;

    public ScheduleServlet() {
        this.scheduleService = ScheduleServiceFactory.getInstance();
        this.objectMapper = ObjectMapperFactory.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
        if (uuid == null) {
            List<Schedule> schedules = this.scheduleService.get();
            List<ScheduleDto> scheduleDtos = schedules.stream().map(ScheduleMapper.INSTANCE::scheduleToScheduleDto).toList();
            resp.getWriter().write(this.objectMapper.writeValueAsString(scheduleDtos));
        } else {
            Schedule schedule = this.scheduleService.get(uuid);
            ScheduleDto scheduleDto = ScheduleMapper.INSTANCE.scheduleToScheduleDto(schedule);
            resp.getWriter().write(this.objectMapper.writeValueAsString(scheduleDto));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        ScheduleCreateDto scheduleCreateDto = this.objectMapper.readValue(inputStream, ScheduleCreateDto.class);
        Schedule schedule = this.scheduleService.save(scheduleCreateDto);
        ScheduleDto scheduleDto = ScheduleMapper.INSTANCE.scheduleToScheduleDto(schedule);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(this.objectMapper.writeValueAsString(scheduleDto));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
        LocalDateTime dtUpdate = PathVariablesSearcherUtil.retrieveDtUpdateAsPathVariables(req, URL_PART_BEFORE_DT_UPDATE_NAME);
        ServletInputStream inputStream = req.getInputStream();
        ScheduleCreateDto scheduleCreateDto = this.objectMapper.readValue(inputStream, ScheduleCreateDto.class);
        Schedule schedule = this.scheduleService.update(scheduleCreateDto, uuid, dtUpdate);
        ScheduleDto scheduleDto = ScheduleMapper.INSTANCE.scheduleToScheduleDto(schedule);
        resp.getWriter().write(this.objectMapper.writeValueAsString(scheduleDto));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
        LocalDateTime dtUpdate = PathVariablesSearcherUtil.retrieveDtUpdateAsPathVariables(req, URL_PART_BEFORE_DT_UPDATE_NAME);
        this.scheduleService.delete(uuid, dtUpdate);
    }
}
