package com.dino.util;

import com.dino.core.Spore;
import com.dino.effects.AcceleratingEffect;
import com.dino.effects.CloneEffect;
import com.dino.effects.ParalyzingEffect;
import com.dino.effects.SlowingEffect;
import com.dino.effects.SporeNoEffect;
import com.dino.effects.StunningEffect;
import com.dino.player.Mycologist;
import com.google.gson.JsonObject;

public class SporeDeserializer {

    public static Spore deserialize(JsonObject obj, EntityRegistry registry) {
        String type = obj.get("type").getAsString();
        String speciesName = obj.get("species").getAsString();
        Mycologist species = (Mycologist) registry.getByName(speciesName);

        if (species == null) {
            throw new IllegalArgumentException("Nem található ilyen Mycologist: " + speciesName);
        }

        switch (type) {
            case "AcceleratingEffect":
                return new AcceleratingEffect(species);
            case "CloneEffect":
                return new CloneEffect(species);
            case "ParalyzingEffect":
                return new ParalyzingEffect(species);
            case "SlowingEffect":
                return new SlowingEffect(species);
            case "SporeNoEffect":
                return new SporeNoEffect(species);
            case "StunningEffect":
                return new StunningEffect(species);
            default:
                throw new IllegalArgumentException("Ismeretlen spóra típus: " + type);
        }
    }
}
