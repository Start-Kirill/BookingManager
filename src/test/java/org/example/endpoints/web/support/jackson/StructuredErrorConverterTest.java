package org.example.endpoints.web.support.jackson;

import org.example.core.dto.errors.SpecificError;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StructuredErrorConverterTest {

    @Test
    void testConvert() {
        StructuredErrorConverter converter = new StructuredErrorConverter();
        Map<String, String> structuredErrors = new HashMap<>();
        structuredErrors.put("field1", "error1");


        List<SpecificError> result = converter.convert(structuredErrors);

        assertEquals(1, result.size());
        assertEquals("field1", result.get(0).getField());
        assertEquals("error1", result.get(0).getMessage());
    }

}