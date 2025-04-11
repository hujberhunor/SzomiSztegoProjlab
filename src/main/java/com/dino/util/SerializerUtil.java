package com.dino.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.*;

public class SerializerUtil {
    public static <T> JsonArray toJsonArray(List<T> list, Function<T, Object> mapper) {
        JsonArray array = new JsonArray();
        for (T item : list) {
            Object value = mapper.apply(item);
            if (value instanceof JsonObject) array.add((JsonObject) value);
            else array.add(value.toString());
        }
        return array;
    }

    public static <K, V> JsonObject toJsonMap(Map<K, V> map, Function<K, String> keyMapper) {
        JsonObject obj = new JsonObject();
        map.forEach((k, v) -> obj.addProperty(keyMapper.apply(k), v.toString()));
        return obj;
    }
}

