package org.example.endpoints.web.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.example.endpoints.web.support.jackson.LocalDateTimeDeserializer;
import org.example.endpoints.web.support.jackson.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class ObjectMapperFactory {

    private static volatile ObjectMapper instance;

    private ObjectMapperFactory() {
    }

    public static ObjectMapper getInstance() {
        if (instance == null) {
            synchronized (ObjectMapperFactory.class) {
                if (instance == null) {
                    SimpleModule simpleModule = new SimpleModule();
                    simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
                    simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

                    instance = new ObjectMapper().registerModule(simpleModule);
                }
            }
        }
        return instance;
    }
}
