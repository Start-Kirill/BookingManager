package org.example.endpoints.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.SupplyCreateDto;
import org.example.core.dto.SupplyDto;
import org.example.core.entity.Supply;
import org.example.core.mappers.SupplyMapper;
import org.example.endpoints.web.factory.ObjectMapperFactory;
import org.example.service.api.ISupplyService;
import org.example.service.factory.SupplyServiceFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = {"/supply", "/supply/*", "/supply/*/dt_update/*"})
public class SupplyServlet extends HttpServlet {

    private final ISupplyService supplyService;

    private final ObjectMapper objectMapper;

    public SupplyServlet() {
        this.supplyService = SupplyServiceFactory.getInstance();
        this.objectMapper = ObjectMapperFactory.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        SupplyCreateDto supplyCreateDto = this.objectMapper.readValue(inputStream, SupplyCreateDto.class);
        Supply supply = this.supplyService.save(supplyCreateDto);
        SupplyDto dto = SupplyMapper.INSTANCE.supplyToSupplyDto(supply);
        resp.setStatus(201);
        resp.getWriter().write(this.objectMapper.writeValueAsString(dto));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = retrieveUserUuidAsPathVariable(req);
        if (uuid == null) {
            List<Supply> supplies = this.supplyService.get();
            List<SupplyDto> suppliesDto = supplies.stream().map(SupplyMapper.INSTANCE::supplyToSupplyDto).toList();
            resp.getWriter().write(this.objectMapper.writeValueAsString(suppliesDto));
        } else {
            Supply supply = this.supplyService.get(uuid);
            SupplyDto supplyDto = SupplyMapper.INSTANCE.supplyToSupplyDto(supply);
            resp.getWriter().write(this.objectMapper.writeValueAsString(supplyDto));
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = retrieveUserUuidAsPathVariable(req);
        LocalDateTime dtUpdate = retrieveDtUpdateAsPathVariables(req);
        if (uuid == null || dtUpdate == null) {
            throw new IllegalArgumentException("Координаты отсутствуют или неверны");
        }
        ServletInputStream inputStream = req.getInputStream();
        SupplyCreateDto supplyCreateDto = this.objectMapper.readValue(inputStream, SupplyCreateDto.class);
        Supply supply = this.supplyService.update(supplyCreateDto, uuid, dtUpdate);
        resp.getWriter().write(this.objectMapper.writeValueAsString(SupplyMapper.INSTANCE.supplyToSupplyDto(supply)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDateTime dtUpdate = retrieveDtUpdateAsPathVariables(req);
        UUID uuid = retrieveUserUuidAsPathVariable(req);
        if (uuid == null || dtUpdate == null) {
            throw new IllegalArgumentException("Координаты отсутствуют или неверны");
        }
        this.supplyService.delete(uuid, dtUpdate);
    }

    private UUID retrieveUserUuidAsPathVariable(HttpServletRequest req) {
        String[] reqURIParts = req.getRequestURI().split("/");
        int index = 0;
        while (!reqURIParts[index++].equals("supply")) ;
        String rawUserUuid;
        UUID userUuid = null;
        if (reqURIParts.length > index) {
            rawUserUuid = reqURIParts[index];
            if (validateUuid(rawUserUuid)) {
                userUuid = UUID.fromString(rawUserUuid);
            }
        }
        return userUuid;
    }

    private LocalDateTime retrieveDtUpdateAsPathVariables(HttpServletRequest req) {
        LocalDateTime dtUpdate = null;
        String[] reqURIParts = req.getRequestURI().split("/");
        int index = 0;
        while (!reqURIParts[index++].equals("dt_update")) ;
        String rawDtUpdate;
        if (reqURIParts.length > index) {
            rawDtUpdate = reqURIParts[index];
            if (validateLocalDateTime(rawDtUpdate)) {
                dtUpdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(rawDtUpdate)), ZoneOffset.UTC);
            }
        }
        return dtUpdate;
    }

    private boolean validateLocalDateTime(String rawDtUpdate) {
        try {
            Long.parseLong(rawDtUpdate);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validateUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }


}
