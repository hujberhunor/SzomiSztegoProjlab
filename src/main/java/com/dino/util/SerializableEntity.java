package com.dino.util;

import com.google.gson.JsonObject;

public interface SerializableEntity {
   JsonObject serialize(ObjectNamer namer);
//    JsonObject serialize();
}