package me.lst.recordplus.util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static <T> void set(Class<T> clazz, String variable, T object, Object value) {
        try {
            Field field = clazz.getDeclaredField(variable);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}