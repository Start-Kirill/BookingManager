package org.example.endpoints.web.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.errors.ErrorResponse;
import org.example.core.dto.errors.StructuredErrorResponse;
import org.example.core.enums.ErrorType;
import org.example.core.exceptions.CommonErrorException;
import org.example.core.exceptions.CommonInternalErrorException;
import org.example.core.exceptions.StructuredErrorException;
import org.example.endpoints.web.factory.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterConfig filterConfig;

    @Mock
    private ServletRequest request;

    private GlobalExceptionHandler globalExceptionHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws ServletException {
        globalExceptionHandler = new GlobalExceptionHandler();
        globalExceptionHandler.init(filterConfig);
        objectMapper = ObjectMapperFactory.getInstance();
    }

    @Test
    void testDoFilterCommonErrorException() throws IOException, ServletException {
        List<ErrorResponse> errors = List.of(new ErrorResponse(ErrorType.ERROR, "Test error"));
        CommonErrorException exception = new CommonErrorException(errors);

        doThrow(exception).when(filterChain).doFilter(request, response);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        globalExceptionHandler.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String actualResponse = stringWriter.toString();
        String expectedResponse = objectMapper.writeValueAsString(errors);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testDoFilterCommonInternalErrorException() throws IOException, ServletException {
        List<ErrorResponse> errors = List.of(new ErrorResponse(ErrorType.ERROR, "Internal error"));
        CommonInternalErrorException exception = new CommonInternalErrorException(errors);

        doThrow(exception).when(filterChain).doFilter(request, response);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        globalExceptionHandler.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String actualResponse = stringWriter.toString();
        String expectedResponse = objectMapper.writeValueAsString(errors);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testDoFilterStructuredErrorException() throws IOException, ServletException {
        Map<String, String> errors = Map.of("field", "error");
        StructuredErrorException exception = new StructuredErrorException(errors);
        StructuredErrorResponse structuredErrorResponse = new StructuredErrorResponse(ErrorType.STRUCTURED_ERROR, errors);

        doThrow(exception).when(filterChain).doFilter(request, response);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        globalExceptionHandler.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String actualResponse = stringWriter.toString();
        String expectedResponse = objectMapper.writeValueAsString(structuredErrorResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testDoFilterUnknownException() throws IOException, ServletException {
        RuntimeException exception = new RuntimeException("Unknown error");

        doThrow(exception).when(filterChain).doFilter(request, response);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        globalExceptionHandler.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        String actualResponse = stringWriter.toString();
        assertEquals("Unknown error", actualResponse);
    }

}