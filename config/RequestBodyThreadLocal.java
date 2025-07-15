package com.tag.biometric.ifService.config;

public class RequestBodyThreadLocal {

    private static final ThreadLocal<String> local = new ThreadLocal<>();
    private static final ThreadLocal<String> roles = new ThreadLocal<>();

    public static void set(String requestId, Boolean isTrue) {
        local.set(requestId);
    }
    public static void set(String role) {
        roles.set(role);
    }

    public static String get() {
        return local.get();
    }
    public static String getRole() {
        return roles.get();
    }

    public static void clear(){
        local.remove();
    }
    public static void clearRole(){
        roles.remove();
    }
}
