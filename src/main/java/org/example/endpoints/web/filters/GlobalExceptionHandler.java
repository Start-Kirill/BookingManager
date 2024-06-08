package org.example.endpoints.web.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.errors.ErrorResponse;
import org.example.core.dto.errors.StructuredErrorResponse;
import org.example.core.enums.ErrorType;
import org.example.core.exceptions.CommonErrorException;
import org.example.core.exceptions.CommonInternalErrorException;
import org.example.core.exceptions.StructuredErrorException;
import org.example.endpoints.web.factory.ObjectMapperFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebFilter("/*")
public class GlobalExceptionHandler implements Filter {

    private static final String SERVER_INTERNAL_ERROR_MESSAGE = "Сервер не способен обработать запрос корректно. Пожалуйста свяжитесь с администратором";

    private ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.objectMapper = ObjectMapperFactory.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            handleException((HttpServletResponse) response, ex);
        }
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        try {
            if (e instanceof CommonErrorException ex) {
                handle(response, ex);
            } else if (e instanceof CommonInternalErrorException ex) {
                handle(response, ex);
            } else if (e instanceof StructuredErrorException ex) {
                handle(response, ex);
            } else {
                handleUnknownError(response, e);
            }
        } catch (IOException ex) {
            handleInternalError(response);
        }

    }

    private void handle(HttpServletResponse response, CommonErrorException ex) throws IOException {
        List<ErrorResponse> errors = ex.getErrors();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(this.objectMapper.writeValueAsString(errors));
    }

    private void handle(HttpServletResponse response, CommonInternalErrorException ex) throws IOException {
        List<ErrorResponse> errors = ex.getErrors();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(this.objectMapper.writeValueAsString(errors));
    }

    private void handle(HttpServletResponse response, StructuredErrorException ex) throws IOException {
        Map<String, String> errors = ex.getErrors();
        StructuredErrorResponse structuredErrorResponse = new StructuredErrorResponse(ErrorType.STRUCTURED_ERROR, errors);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(this.objectMapper.writeValueAsString(structuredErrorResponse));
    }

    private void handleInternalError(HttpServletResponse response) throws IOException {
        List<ErrorResponse> errorResponses = List.of(new ErrorResponse(ErrorType.ERROR, SERVER_INTERNAL_ERROR_MESSAGE));
        response.getWriter().write(this.objectMapper.writeValueAsString(errorResponses));
    }

    private void handleUnknownError(HttpServletResponse response, Exception ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write(ex.getMessage());
    }

}
