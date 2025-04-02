package com.dino.util;

import java.util.Objects;

public class Logger {

    public void logChange(String objectType, String objectName, String property, Object prev, Object next) {
        if (!Objects.equals(prev, next)) {
            logOk(objectType, objectName, property, String.valueOf(prev), String.valueOf(next));
        }
    }

    public void logOk(String objectType, String objectName, String property, String oldState, String newState) {
        System.out.printf("[OK] %s %s %s: %s -> %s%n",
            objectType.toUpperCase(), objectName, property.toUpperCase(), oldState, newState);
    }

    public void logError(String objectType, String objectName, String message) {
        System.out.printf("[ERROR] %s %s %s%n",
            objectType.toUpperCase(), objectName, message);
    }
}

 