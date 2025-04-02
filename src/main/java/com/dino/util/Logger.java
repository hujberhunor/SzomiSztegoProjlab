package com.dino.util;

import java.util.Objects;

public class Logger {
    private final EntityRegistry registry;

    public Logger(EntityRegistry registry) {
        this.registry = registry;
    }

    public void logChange(String objectType, Object obj, String property, Object oldVal, Object newVal) {
        if (!Objects.equals(oldVal, newVal)) {
            String name = registry.getNameOf(obj);
            logOk(objectType, name, property, String.valueOf(oldVal), String.valueOf(newVal));
        }
    }

    public void logOk(String objectType, String objectName, String property, String oldState, String newState) {
        System.out.printf("[OK] %s %s %s: %s -> %s%n",
            objectType.toUpperCase(), objectName, property.toUpperCase(), oldState, newState);
    }

    public void logError(String objectType, String objectName, String errorMsg) {
        System.out.printf("[ERROR] %s %s %s%n",
            objectType.toUpperCase(), objectName, errorMsg);
    }
}
