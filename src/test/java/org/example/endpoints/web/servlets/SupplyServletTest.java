package org.example.endpoints.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.ScheduleCreateDto;
import org.example.core.dto.SupplyCreateDto;
import org.example.core.entity.Supply;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.service.api.ISupplyService;
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
class SupplyServletTest {

    @Mock
    private ISupplyService supplyService;

    @InjectMocks
    private SupplyServlet supplyServlet;

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

    private SupplyCreateDto supplyCreateDto;

    private Supply supply;

    @BeforeEach
    void setUp() throws Exception {
        now = LocalDateTime.now();
        UUID masterUuid = UUID.randomUUID();
        User master = new User(masterUuid, "Kirill", "+123465789", UserRole.MASTER, new ArrayList<>(), now, now);
        uuid = UUID.randomUUID();
        supplyCreateDto = new SupplyCreateDto("Supply test", BigDecimal.valueOf(100.5), 100, List.of(master.getUuid()));
        supply = new Supply(uuid, "Supply test", BigDecimal.valueOf(100.5), 100, List.of(master), now, now);
        master.setSupplies(List.of(supply));

        injectMock(supplyServlet, "supplyService", supplyService);
    }

    @Test
    void testDoGetList() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/supply");
        when(supplyService.get()).thenReturn(List.of(supply));
        when(response.getWriter()).thenReturn(printWriter);

        supplyServlet.doGet(request, response);

        verify(supplyService).get();
        verify(response).getWriter();
    }

    @Test
    void testDoGetByUuid() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/supply/" + uuid.toString());
        when(supplyService.get(any(UUID.class))).thenReturn(supply);
        when(response.getWriter()).thenReturn(printWriter);

        supplyServlet.doGet(request, response);

        verify(supplyService).get(uuid);
        verify(response).getWriter();
    }

    @Test
    void testDoPost() throws Exception {
        injectMock(supplyServlet, "objectMapper", objectMapper);
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(any(ServletInputStream.class), eq(SupplyCreateDto.class))).thenReturn(supplyCreateDto);
        when(supplyService.save(any(SupplyCreateDto.class))).thenReturn(supply);
        when(response.getWriter()).thenReturn(printWriter);

        supplyServlet.doPost(request, response);

        verify(supplyService).save(any(SupplyCreateDto.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).getWriter();
    }

    @Test
    void testDoPut() throws Exception {
        injectMock(supplyServlet, "objectMapper", objectMapper);
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/supply/" + uuid.toString() + "/dt_update/" + now.toInstant(ZoneOffset.UTC).toEpochMilli());
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(any(ServletInputStream.class), eq(SupplyCreateDto.class))).thenReturn(supplyCreateDto);
        when(supplyService.update(any(SupplyCreateDto.class), any(UUID.class), any(LocalDateTime.class))).thenReturn(supply);
        when(response.getWriter()).thenReturn(printWriter);

        supplyServlet.doPut(request, response);

        verify(supplyService).update(any(SupplyCreateDto.class), any(UUID.class), any(LocalDateTime.class));
        verify(response).getWriter();
    }

    @Test
    void testDoDelete() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/supply/" + uuid.toString() + "/dt_update/" + now.toInstant(ZoneOffset.UTC).toEpochMilli());

        supplyServlet.doDelete(request, response);

        verify(supplyService).delete(any(UUID.class), any(LocalDateTime.class));
    }

    private void injectMock(Object target, String fieldName, Object mock) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, mock);
    }
}