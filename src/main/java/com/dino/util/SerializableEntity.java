package com.dino.util;

import com.google.gson.JsonObject;

public interface SerializableEntity {
    // JsonObject serialize(EntityRegistry registry, Logger logger);
    JsonObject serialize(ObjectNamer namer, Logger logger);

}