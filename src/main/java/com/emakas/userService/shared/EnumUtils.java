package com.emakas.userService.shared;

public class EnumUtils {
    public static <E extends Enum<E>> E parseOrDefault(Class<E> enumClass, String value, E defaultValue) {
        if (StringUtils.isNullOrEmpty(value))
            return defaultValue;
        try {
            return E.valueOf(enumClass, value);
        }
        catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }
}
