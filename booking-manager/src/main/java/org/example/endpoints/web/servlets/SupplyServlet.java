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
import org.example.endpoints.web.util.PathVariablesSearcherUtil;
import org.example.service.api.ISupplyService;
import org.example.service.factory.SupplyServiceFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = {"/supply", "/supply/*", "/supply/*/dt_update/*"})
public class SupplyServlet extends HttpServlet {

    private static final String URL_PART_BEFORE_UUID_NAME = "supply";

    private static final String URL_PART_BEFORE_DT_UPDATE_NAME = "dt_update";

    private final ISupplyService supplyService;

    private final ObjectMapper objectMapper;

    public SupplyServlet() {
        this.supplyService = SupplyServiceFactory.getInstance();
        this.objectMapper = ObjectMapperFactory.getInstance();
    }

    public SupplyServlet(ISupplyService supplyService, ObjectMapper objectMapper) {
        this.supplyService = supplyService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream inputStream = req.getInputStream();
        SupplyCreateDto supplyCreateDto = this.objectMapper.readValue(inputStream, SupplyCreateDto.class);
        Supply supply = this.supplyService.save(supplyCreateDto);
        SupplyDto dto = SupplyMapper.INSTANCE.supplyToSupplyDto(supply);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(this.objectMapper.writeValueAsString(dto));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
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
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
        LocalDateTime dtUpdate = PathVariablesSearcherUtil.retrieveDtUpdateAsPathVariables(req, URL_PART_BEFORE_DT_UPDATE_NAME);
        ServletInputStream inputStream = req.getInputStream();
        SupplyCreateDto supplyCreateDto = this.objectMapper.readValue(inputStream, SupplyCreateDto.class);
        Supply supply = this.supplyService.update(supplyCreateDto, uuid, dtUpdate);
        resp.getWriter().write(this.objectMapper.writeValueAsString(SupplyMapper.INSTANCE.supplyToSupplyDto(supply)));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDateTime dtUpdate = PathVariablesSearcherUtil.retrieveDtUpdateAsPathVariables(req, URL_PART_BEFORE_DT_UPDATE_NAME);
        UUID uuid = PathVariablesSearcherUtil.retrieveUuidAsPathVariable(req, URL_PART_BEFORE_UUID_NAME);
        this.supplyService.delete(uuid, dtUpdate);
    }

}
