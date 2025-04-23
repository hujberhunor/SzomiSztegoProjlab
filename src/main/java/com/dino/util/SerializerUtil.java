package com.dino.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SerializerUtil {
    // public static <T> JsonArray toJsonArray(List<T> list, Function<T, Object> mapper) {
    //     JsonArray array = new JsonArray();
    //     for (T item : list) {
    //         Object value = mapper.apply(item);
    //         if (value instanceof JsonObject)
    //             array.add((JsonObject) value);
    //         else
    //             array.add(value.toString());
    //     }
    //     return array;
    // }

    public static <T> JsonArray toJsonArray(List<T> list, Function<T, Object> mapper) {
        JsonArray array = new JsonArray();
        for (T item : list) {
            Object value = mapper.apply(item);
            if (value == null) {
                array.add("null"); // vagy array.add((JsonElement) JsonNull.INSTANCE);
            } else if (value instanceof JsonObject) {
                array.add((JsonObject) value);
            } else {
                array.add(value.toString());
            }
        }
        return array;
    }

    public static <K, V> JsonObject toJsonMap(Map<K, V> map, Function<K, String> keyMapper) {
        JsonObject obj = new JsonObject();
        map.forEach((k, v) -> obj.addProperty(keyMapper.apply(k), v.toString()));
        return obj;
    }
}
