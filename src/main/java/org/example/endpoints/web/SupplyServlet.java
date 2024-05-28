package org.example.endpoints.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.example.service.api.ISupplyService;

@WebServlet("/supply")
public class SupplyServlet extends HttpServlet {

    private final ISupplyService supplyService;

    private final ObjectMapper objectMapper;

    public SupplyServlet(ISupplyService supplyService, ObjectMapper objectMapper) {
        this.supplyService = supplyService;
        this.objectMapper = objectMapper;
    }
}
