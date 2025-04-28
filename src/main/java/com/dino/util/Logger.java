package com.dino.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Logger {

    private static Logger instance;

    private final StringBuilder logBuffer = new StringBuilder();
    private final EntityRegistry registry = EntityRegistry.getInstance();

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public static void reset() {
        instance = new Logger();
    }

    private void writeLog(String line) {
        System.out.println(line);
        logBuffer.append(line).append(System.lineSeparator());
    }

    public void logChange(
        String objectType,
        Object obj,
        String property,
        Object oldVal,
        Object newVal
    ) {
        if (!Objects.equals(oldVal, newVal)) {
            String name = registry.getNameOf(obj);
            String oldValStr = formatValue(oldVal);
            String newValStr = formatValue(newVal);
            logOk(objectType, name, property, oldValStr, newValStr);
        }
    }

    private String formatValue(Object val) {
        if (val == null) return "null";
        String registeredName = registry.getNameOf(val);
        if (registeredName != null) {
            return registeredName;
        }
        return val.toString();
    }

    public void logOk(
        String objectType,
        Object obj,
        String property,
        String oldValue,
        String newValue
    ) {
        String name = registry.getNameOf(obj); // mindig registry alapján
        writeLog(
            String.format(
                "[OK] %s %s %s: %s -> %s",
                objectType.toUpperCase(),
                name,
                property.toUpperCase(),
                oldValue,
                newValue
            )
        );
    }

    public void logOk(
        String objectType,
        String objectName,
        String property,
        String oldValue,
        String newValue
    ) {
        writeLog(
            String.format(
                "[OK] %s %s %s: %s -> %s",
                objectType.toUpperCase(),
                objectName,
                property.toUpperCase(),
                oldValue,
                newValue
            )
        );
    }

    public void logError(
        String objectType,
        String objectName,
        String errorMsg
    ) {
        writeLog(
            String.format(
                "[ERROR] %s %s %s",
                objectType.toUpperCase(),
                objectName,
                errorMsg
            )
        );
    }

    public String getLog() {
        return logBuffer.toString();
    }

    public void saveLogToFile(String filePath) {
        try {
            Files.writeString(Path.of(filePath), logBuffer.toString());
        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to save log to file: " + filePath,
                e
            );
        }
    }
}
