package com.dino.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class EntityRegistry {
    private static EntityRegistry instance;

    private final Map<String, Object> nameToObject = new HashMap<>();
    private final Map<Object, String> objectToName = new IdentityHashMap<>();

    private EntityRegistry() {}

    public static EntityRegistry getInstance() {
        if (instance == null) {
            instance = EntityRegistry.getInstance();
        }
        return instance;
    }

    public static void reset() {
        instance = EntityRegistry.getInstance();
    }

    public void register(String name, Object obj) {
        nameToObject.put(name, obj);
        objectToName.put(obj, name);
    }

    public Object getByName(String name) {
        return nameToObject.get(name);
    }

    public String getNameOf(Object obj) {
        return objectToName.get(obj);
    }

    public boolean isRegistered(Object obj) {
        return objectToName.containsKey(obj);
    }
}
