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

    /**
     * Deserializes a Spore object from a JSON object.
     *
     * @param obj The JSON object to deserialize
     * @param registry The entity registry to use for resolving references
     * @return The deserialized Spore object, or null if deserialization failed
     */
    public static Spore deserialize(JsonObject obj, EntityRegistry registry) {
        // Validate required fields
        if (!obj.has("type") || !obj.has("species")) {
            System.err.println(
                "Error deserializing spore: missing required fields (type or species)"
            );
            return null;
        }

        String type = obj.get("type").getAsString();
        String speciesName = obj.get("species").getAsString();
        Mycologist species = (Mycologist) registry.getByName(speciesName);

        if (species == null) {
            System.err.println(
                "Error deserializing spore: could not find mycologist '" +
                speciesName +
                "'"
            );
            return null;
        }

        // Create the appropriate spore type
        Spore spore = null;
        try {
            switch (type) {
                case "AcceleratingEffect":
                    spore = new AcceleratingEffect(species);
                    break;
                case "CloneEffect":
                    spore = new CloneEffect(species);
                    break;
                case "ParalyzingEffect":
                    spore = new ParalyzingEffect(species);
                    break;
                case "SlowingEffect":
                    spore = new SlowingEffect(species);
                    break;
                case "SporeNoEffect":
                    spore = new SporeNoEffect(species);
                    break;
                case "StunningEffect":
                    spore = new StunningEffect(species);
                    break;
                default:
                    System.err.println(
                        "Error deserializing spore: unknown type '" + type + "'"
                    );
                    return null;
            }
        } catch (Exception e) {
            System.err.println(
                "Error creating spore instance: " + e.getMessage()
            );
            e.printStackTrace();
            return null;
        }

        // Set additional properties if available
        try {
            // Set duration if available
            if (obj.has("effectDuration")) {
                int duration = obj.get("effectDuration").getAsInt();
                // The effectDuration in the stored object is how many turns are left,
                // so we need to adjust it - Spore starts with duration=2 by default
                int initialDuration = 2;
                int decrease = initialDuration - duration;

                // Apply the decrease
                for (int i = 0; i < decrease; i++) {
                    spore.decreaseEffectDuration();
                }
            }

            // If there's a name property, register the spore with that name
            if (obj.has("name") && !obj.get("name").isJsonNull()) {
                String name = obj.get("name").getAsString();
                ObjectNamer.getInstance().register(name, spore);
            } else {
                ObjectNamer.getInstance().register(spore);
            }
        } catch (Exception e) {
            System.err.println(
                "Error setting spore properties: " + e.getMessage()
            );
            e.printStackTrace();
        }

        return spore;
    }
}
