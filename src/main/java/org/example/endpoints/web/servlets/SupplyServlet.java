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
import org.example.endpoints.web.factory.ObjectMapperFactory;
import org.example.service.api.ISupplyService;
import org.example.service.factory.SupplyServiceFactory;

import java.io.IOException;

@WebServlet("/supply")
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
        SupplyDto supplyDto = new SupplyDto();
        supplyDto.setUuid(supply.getUuid());
        supplyDto.setName(supply.getName());
        supplyDto.setPrice(supply.getPrice());
        supplyDto.setDtCreate(supply.getDtCreate());
        supplyDto.setDtUpdate(supply.getDtUpdate());
        resp.getWriter().write(this.objectMapper.writeValueAsString(supplyDto));
    }

}
