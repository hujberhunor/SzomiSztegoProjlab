package com.dino.core;

import com.dino.effects.AcceleratingEffect;
import com.dino.effects.CloneEffect;
import com.dino.effects.ParalyzingEffect;
import com.dino.effects.SlowingEffect;
import com.dino.effects.SporeNoEffect;
import com.dino.effects.StunningEffect;
import com.dino.player.Mycologist;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.google.gson.JsonObject;

//Absztrakt osztály, aminek leszármazottai a specifikus spóratípusokat valósítják meg.
//Az osztály attribútumaiban számontartja, hogy melyik gombászhoz tartozik a spóra, hány körig tart a hatása,
//illetve hogy elfogyasztásakor a rovarász mennyi pontot kap (ez a tápanyagtartalom).
//A spóra altípusok ősosztálya
public abstract class Spore implements SerializableEntity {
    // Attribútumok. Sorrendben: a gombász, akitől ered a spóra, hatásának hossza
    // illetve a tápanyagtartalom
    protected Mycologist species;
    protected int effectDuration;
    protected int nutrientValue;

    // Default konstruktor
    public Spore(Mycologist mycologist, int nutrientVal) {
        species = mycologist;
        effectDuration = 2;
        nutrientValue = nutrientVal;
    }

    public int getEffectDuration() {
        return effectDuration;
    }

    public int getNutrientValue() {
        return nutrientValue;
    }

    // Eggyel csökkenti a hátralévő körök számát, amelyek eltelte után a hatás
    // elveszik
    public void decreaseEffectDuration() {
        if (effectDuration > 0)
            effectDuration--;
    }

    // Absztrakt függvény, amit a leszármazott spóratípusok megvalósítanak. Ezzel a
    // függvénnyel fejtik ki hatásukat a paraméterként átadott rovaron.
    public abstract void applyTo(Insect i);

    // Absztarkt függvény, ami visszaadja a spóra effektjét reprezentáló integert.
    public abstract int sporeType();

    @Override
    public JsonObject serialize(ObjectNamer namer, Logger logger) {
        JsonObject obj = new JsonObject();

        // Típus (osztálynév)
        obj.addProperty("type", this.getClass().getSimpleName());

        // Gombász név
        String speciesName = namer.getNameOf(species);
        if (speciesName != null) {
            obj.addProperty("species", speciesName);
        } else {
            logger.logError("Spore", "?", "Ismeretlen vagy nem regisztrált species: " + species);
        }

        // Effekt hossz és tápanyagtartalom
        obj.addProperty("effectDuration", effectDuration);
        obj.addProperty("nutrientValue", nutrientValue);

        return obj;
    }

    public static Spore deserialize(JsonObject obj, ObjectNamer namer, Logger logger) {
        String type = obj.get("type").getAsString();
        String speciesName = obj.get("species").getAsString();
        Object spec = namer.getByName(speciesName);

        if (!(spec instanceof Mycologist)) {
            logger.logError("Spore", speciesName, "Nem Mycologist típus");
            return null;
        }

        Mycologist mycologist = (Mycologist) spec;
        int duration = obj.get("effectDuration").getAsInt();
        int nutrient = obj.get("nutrientValue").getAsInt();

        Spore spore;

        switch (type) {
            case "StunningEffect":
                spore = new StunningEffect(mycologist);
                break;
            case "ParalyzingEffect":
                spore = new ParalyzingEffect(mycologist);
                break;
            case "SlowingEffect":
                spore = new SlowingEffect(mycologist);
                break;
            case "AcceleratingEffect":
                spore = new AcceleratingEffect(mycologist);
                break;
            case "SporeNoEffect":
                spore = new SporeNoEffect(mycologist);
                break;
            case "CloneEffect":
                spore = new CloneEffect(mycologist); // ha kell neki külön paraméter, itt érdemes logolni a hiányt
                break;
            default:
                logger.logError("Spore", type, "Ismeretlen Spore típus");
                return null;
        }

        // Állapotok visszaállítása
        spore.effectDuration = duration;
        spore.nutrientValue = nutrient;

        return spore;
    }

}