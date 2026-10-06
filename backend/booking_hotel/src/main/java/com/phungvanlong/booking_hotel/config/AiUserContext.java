package com.phungvanlong.booking_hotel.config;

public class AiUserContext {
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static String get() {
        return CONTEXT.get();
    }

    public static void set(String userEmail) {
        CONTEXT.set(userEmail);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
