package org.example.endpoints.web.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.core.dto.ScheduleCreateDto;
import org.example.core.entity.Schedule;
import org.example.core.entity.User;
import org.example.core.enums.UserRole;
import org.example.service.api.IScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
class ScheduleServletTest {

    @Mock
    private IScheduleService scheduleService;

    @InjectMocks
    private ScheduleServlet scheduleServlet;

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
    private ScheduleCreateDto scheduleCreateDto;
    private Schedule schedule;

    @BeforeEach
    void setUp() throws Exception {
        now = LocalDateTime.now();
        UUID masterUuid = UUID.randomUUID();
        User master = new User(masterUuid, "Kirill", "+123465789", UserRole.MASTER, new ArrayList<>(), now, now);
        uuid = UUID.randomUUID();
        scheduleCreateDto = new ScheduleCreateDto(masterUuid, now.plusDays(1), now.plusDays(2));
        schedule = new Schedule(uuid, master, now.plusDays(1), now.plusDays(2), now, now);

        injectMock(scheduleServlet, "scheduleService", scheduleService);
    }

    @Test
    void testDoGetList() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/schedule");
        when(scheduleService.get()).thenReturn(List.of(schedule));
        when(response.getWriter()).thenReturn(printWriter);

        scheduleServlet.doGet(request, response);

        verify(scheduleService).get();
        verify(response).getWriter();
    }

    @Test
    void testDoGetByUuid() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/schedule/" + uuid.toString());
        when(scheduleService.get(any(UUID.class))).thenReturn(schedule);
        when(response.getWriter()).thenReturn(printWriter);

        scheduleServlet.doGet(request, response);

        verify(scheduleService).get(uuid);
        verify(response).getWriter();
    }

    @Test
    void testDoPost() throws Exception {
        injectMock(scheduleServlet, "objectMapper", objectMapper);
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(any(ServletInputStream.class), eq(ScheduleCreateDto.class))).thenReturn(scheduleCreateDto);
        when(scheduleService.save(any(ScheduleCreateDto.class))).thenReturn(schedule);
        when(response.getWriter()).thenReturn(printWriter);

        scheduleServlet.doPost(request, response);

        verify(scheduleService).save(any(ScheduleCreateDto.class));
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        verify(response).getWriter();
    }

    @Test
    void testDoPut() throws Exception {
        injectMock(scheduleServlet, "objectMapper", objectMapper);
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/schedule/" + uuid.toString() + "/dt_update/" + now.toInstant(ZoneOffset.UTC).toEpochMilli());
        when(request.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(any(ServletInputStream.class), eq(ScheduleCreateDto.class))).thenReturn(scheduleCreateDto);
        when(scheduleService.update(any(ScheduleCreateDto.class), any(UUID.class), any(LocalDateTime.class))).thenReturn(schedule);
        when(response.getWriter()).thenReturn(printWriter);

        scheduleServlet.doPut(request, response);

        verify(scheduleService).update(any(ScheduleCreateDto.class), any(UUID.class), any(LocalDateTime.class));
        verify(response).getWriter();
    }

    @Test
    void testDoDelete() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("http://localhost:8080/BookingManager-1.0-SNAPSHOT/schedule/" + uuid.toString() + "/dt_update/" + now.toInstant(ZoneOffset.UTC).toEpochMilli());

        scheduleServlet.doDelete(request, response);

        verify(scheduleService).delete(any(UUID.class), any(LocalDateTime.class));
    }

    private void injectMock(Object target, String fieldName, Object mock) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, mock);
    }
}