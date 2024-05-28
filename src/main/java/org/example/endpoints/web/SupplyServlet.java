package org.example.endpoints.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.example.service.api.ISupplyService;

@WebServlet("/supply")
public class SupplyServlet extends HttpServlet {

    private final ISupplyService supplyService;

    public SupplyServlet(ISupplyService supplyService) {
        this.supplyService = supplyService;
    }
}
