package org.example.endpoints.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.SupplyCreateDto;
import org.example.core.dto.UserCreateDto;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.service.api.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServletTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserServlet userServlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ServletInputStream inputStream;

    private UUID uuid;
    private LocalDateTime now;

    private UserCreateDto userCreateDto;

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        now = LocalDateTime.now();
        UUID supplyUuid = UUID.randomUUID();
        Supply supply = new Supply(supplyUuid, "Test supply", BigDecimal.valueOf(100), 100, new ArrayList<>(), now, now);
        uuid = UUID.randomUUID();
        userCreateDto = new UserCreateDto("User test", "+12345678", UserRole.MASTER, List.of(supplyUuid));
        user = new User(uuid, "User test", "+12345678", UserRole.MASTER, List.of(supply), now, now);
        supply.setMasters(List.of(user));
    }

    @Test
    void testDoGetList() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/user");
        when(userService.get()).thenReturn(List.of(user));
        when(response.getWriter()).thenReturn(printWriter);

        userServlet.doGet(request, response);

        verify(userService).get();
        verify(response).getWriter();
    }

    @Test
    void testDoGetByUuid() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/user/" + uuid.toString());
        when(userService.get(any(UUID.class))).thenReturn(user);
        when(response.getWriter()).thenReturn(printWriter);

        userServlet.doGet(request, response);

        verify(userService).get(uuid);
        verify(response).getWriter();
    }

    @Test
    void testDoPost() throws Exception {
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(any(ServletInputStream.class), eq(UserCreateDto.class))).thenReturn(userCreateDto);
        when(userService.save(any(UserCreateDto.class))).thenReturn(user);
        when(response.getWriter()).thenReturn(printWriter);

        userServlet.doPost(request, response);

        verify(userService).save(any(UserCreateDto.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).getWriter();
    }

    @Test
    void testDoPut() throws Exception {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/user/" + uuid.toString() + "/dt_update/" + now.toInstant(ZoneOffset.UTC).toEpochMilli());
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(any(ServletInputStream.class), eq(UserCreateDto.class))).thenReturn(userCreateDto);
        when(userService.update(any(UserCreateDto.class), any(UUID.class), any(LocalDateTime.class))).thenReturn(user);
        when(response.getWriter()).thenReturn(printWriter);

        userServlet.doPut(request, response);

        verify(userService).update(any(UserCreateDto.class), any(UUID.class), any(LocalDateTime.class));
        verify(response).getWriter();
    }

    @Test
    void testDoDelete() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/user/" + uuid.toString() + "/dt_update/" + now.toInstant(ZoneOffset.UTC).toEpochMilli());

        userServlet.doDelete(request, response);

        verify(userService).delete(any(UUID.class), any(LocalDateTime.class));
    }
}