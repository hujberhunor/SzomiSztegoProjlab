package com.dino.util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.*;

public class Serializer {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveToFile(SerializableEntity entity, String filename, EntityRegistry registry) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(entity.serialize(registry), writer);
        }
    }

    public static JsonObject loadFromFile(String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            return gson.fromJson(reader, JsonObject.class);
        }
    }
}
