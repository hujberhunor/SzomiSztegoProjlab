package com.dino.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Logger {
    private final EntityRegistry registry;
    private final StringBuilder logBuffer = new StringBuilder();

    public Logger(EntityRegistry registry) {
        this.registry = registry;
    }

    private void writeLog(String line) {
        System.out.println(line); // Konzolra mindig megy!
        logBuffer.append(line).append(System.lineSeparator()); // Bufferbe is mindig megy!
    }

    public void logChange(String objectType, Object obj, String property, Object oldVal, Object newVal) {
        if (!Objects.equals(oldVal, newVal)) {
            String name = registry.getNameOf(obj);
            logOk(objectType, name, property, String.valueOf(oldVal), String.valueOf(newVal));
        }
    }

    public void logOk(String objectType, String objectName, String property, String oldState, String newState) {
        writeLog(String.format("[OK] %s %s %s: %s -> %s",
            objectType.toUpperCase(), objectName, property.toUpperCase(), oldState, newState));
    }

    public void logError(String objectType, String objectName, String errorMsg) {
        writeLog(String.format("[ERROR] %s %s %s",
            objectType.toUpperCase(), objectName, errorMsg));
    }

    public String getLog() {
        return logBuffer.toString();
    }

    public void saveLogToFile(String filePath) {
        try {
            Files.writeString(Path.of(filePath), logBuffer.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save log to file: " + filePath, e);
        }
    }
}
