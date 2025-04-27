package com.dino.core;

import com.dino.player.Mycologist;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

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

    //az applyTo() függvényhez a logChange összehasonlítása miatt kell
    protected class EffectList{
        List<Spore> effects = new ArrayList<Spore>();

        public EffectList(List<Spore> effects) {
            this.effects.addAll(effects);
        }

        public List<Spore> getList() {
            return effects;
        }

        @Override
        public String toString() {
            return effects.toString();
        }
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = new JsonObject();

        obj.addProperty("type", this.getClass().getSimpleName());
        obj.addProperty("species", namer.getName(species));
        obj.addProperty("effectDuration", effectDuration);
        obj.addProperty("nutrientValue", nutrientValue);

        return obj;
    }
}
