package com.minis.utils;

public class WrapperClassUtils {
    /**
     * 判断当前类型是否属于：基本数据类型、包装类、String类型
     * @param clazz
     * @return
     */
    public static boolean isWrapperClass(Class<?> clazz) {
        return clazz.isPrimitive() || isWrapperClassByType(clazz) || isStringType(clazz);
    }

    private static boolean isStringType(Class<?> clazz) {
        return clazz == String.class;
    }

    private static boolean isWrapperClassByType(Class<?> clazz) {
        return clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Short.class ||
                clazz == Byte.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == Boolean.class ||
                clazz == Character.class;
    }
}
