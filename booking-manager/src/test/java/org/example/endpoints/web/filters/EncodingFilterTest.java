package org.example.endpoints.web.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EncodingFilterTest {
    private EncodingFilter encodingFilter;

    @Mock
    private ServletRequest servletRequest;

    @Mock
    private ServletResponse servletResponse;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        encodingFilter = new EncodingFilter();
    }

    @Test
    void testDoFilter() throws IOException, ServletException {
        encodingFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(servletResponse).setCharacterEncoding("UTF-8");
        verify(servletResponse).setContentType("application/json");
        verify(filterChain).doFilter(servletRequest, servletResponse);
    }
}