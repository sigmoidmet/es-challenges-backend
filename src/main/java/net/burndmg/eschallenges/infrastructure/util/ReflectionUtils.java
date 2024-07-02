package net.burndmg.eschallenges.infrastructure.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@UtilityClass
public class ReflectionUtils {

    public static String[] getNonStaticFieldsNames(Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                     .filter(field -> !Modifier.isStatic(field.getModifiers()))
                     .map(Field::getName)
                     .toArray(String[]::new);
    }
}
