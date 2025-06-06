package com.dino.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Serializer {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveToFile(SerializableEntity entity, ObjectNamer namer, String filename)
            throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(entity.serialize(namer), writer);
        }
    }

    public static void saveJsonToFile(JsonObject obj, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(obj, writer);
        }
    }

    public static JsonObject loadFromFile(String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            return gson.fromJson(reader, JsonObject.class);
        }
    }
}
