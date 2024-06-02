package org.example.core.util;

public class NullCheckUtil {

    private NullCheckUtil() {
    }

    public static void checkNull(String message, Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
