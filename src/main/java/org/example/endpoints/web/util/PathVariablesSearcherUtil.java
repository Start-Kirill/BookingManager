package org.example.endpoints.web.util;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class PathVariablesSearcherUtil {

    private PathVariablesSearcherUtil() {
    }

    public static UUID retrieveUuidAsPathVariable(HttpServletRequest req, String urlPartName) {
        String[] reqURIParts = req.getRequestURI().split("/");
        int index = 0;
        while (!reqURIParts[index++].equals(urlPartName)) ;
        String rawUserUuid;
        UUID userUuid = null;
        if (reqURIParts.length > index) {
            rawUserUuid = reqURIParts[index];
            if (validateUuid(rawUserUuid)) {
                userUuid = UUID.fromString(rawUserUuid);
            }
        }
        return userUuid;
    }

    public static LocalDateTime retrieveDtUpdateAsPathVariables(HttpServletRequest req, String urlPartName) {
        LocalDateTime dtUpdate = null;
        String[] reqURIParts = req.getRequestURI().split("/");
        int index = 0;
        while (!reqURIParts[index++].equals(urlPartName)) ;
        String rawDtUpdate;
        if (reqURIParts.length > index) {
            rawDtUpdate = reqURIParts[index];
            if (validateLocalDateTime(rawDtUpdate)) {
                dtUpdate = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(rawDtUpdate)), ZoneOffset.UTC);
            }
        }
        return dtUpdate;
    }

    private static boolean validateLocalDateTime(String rawDtUpdate) {
        try {
            Long.parseLong(rawDtUpdate);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validateUuid(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
