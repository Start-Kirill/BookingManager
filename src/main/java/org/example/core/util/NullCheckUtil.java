package org.example.core.util;

import org.example.core.dto.errors.ErrorResponse;
import org.example.core.enums.ErrorType;
import org.example.core.exceptions.NullArgumentException;

import java.util.List;

public class NullCheckUtil {

    private NullCheckUtil() {
    }

    public static void checkNull(String message, Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                throw new NullArgumentException(List.of(new ErrorResponse(ErrorType.ERROR, message)));
            }
        }
    }
}
