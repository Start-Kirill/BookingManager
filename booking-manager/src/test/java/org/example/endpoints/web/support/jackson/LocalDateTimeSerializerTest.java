package org.example.endpoints.web.support.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LocalDateTimeSerializerTest {

    private LocalDateTimeSerializer localDateTimeSerializer;

    @Mock
    private JsonGenerator jsonGenerator;

    @Mock
    private SerializerProvider serializerProvider;

    @BeforeEach
    void setUp() {
        localDateTimeSerializer = new LocalDateTimeSerializer();
    }

    @Test
    void testSerialize() throws IOException {
        LocalDateTime localDateTime = LocalDateTime.of(2023, Month.JUNE, 9, 12, 0, 0);
        long expectedMillis = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        localDateTimeSerializer.serialize(localDateTime, jsonGenerator, serializerProvider);

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(jsonGenerator).writeNumber(captor.capture());
        long actualMillis = captor.getValue();

        assertEquals(expectedMillis, actualMillis);
    }

}