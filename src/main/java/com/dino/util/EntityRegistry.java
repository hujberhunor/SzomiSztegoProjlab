package com.dino.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Ez tartja számon, hogy melyik OBJEKTUMNAK mi a NEVE
 * Logger osztály miatt kell. Tudni akarjuk, hogy melyik objektum
 * változását követjük. 
 */
public class EntityRegistry {
    private final Map<String, Object> nameToObject = new HashMap<>();
    private final Map<Object, String> objectToName = new IdentityHashMap<>();

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
