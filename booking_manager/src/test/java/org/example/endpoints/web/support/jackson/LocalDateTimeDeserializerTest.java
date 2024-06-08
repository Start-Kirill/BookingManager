package org.example.endpoints.web.support.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeDeserializerTest {

    @Test
    void testDeserialize() throws IOException {
        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer();
        JsonParser jsonParser = Mockito.mock(JsonParser.class);
        DeserializationContext deserializationContext = Mockito.mock(DeserializationContext.class);
        long millis = 1623312000000L; // 2021-06-10T00:00:00Z

        Mockito.when(jsonParser.getLongValue()).thenReturn(millis);

        LocalDateTime result = deserializer.deserialize(jsonParser, deserializationContext);

        LocalDateTime expected = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
        assertEquals(expected, result);
    }
}