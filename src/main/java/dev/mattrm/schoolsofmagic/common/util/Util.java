package dev.mattrm.schoolsofmagic.common.util;

public final class Util {
    public static <T> T ifNullThen(T obj, T other) {
        return obj != null ? obj : other;
    }
}
