package com.example_2.haidaodemo_v1.common.Utils;

public class BaseContext {
    private static final ThreadLocal<Long> localThread = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        localThread.set(userId);
    }

    public static Long getUserId() {
        return localThread.get();
    }

    public static void removeUserId() {
        localThread.remove();
    }
}
