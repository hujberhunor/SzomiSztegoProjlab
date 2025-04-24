package com.dino.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A gombafonalakat reprezentáló osztály. Egy objektum a növesztés sorrendjében
 * tartalmazza a tektonokat,
 * amiken keresztül nő, hogy a fonál elszakadása esetén (rovar vagy törés
 * hatására) a szekvenciából egyértelmű
 * legyen, hogy a fonál melyik fele nem kapcsolódik már a gombatestből, amiből
 * származnak.
 */
public class Hypha implements SerializableEntity {

    /**
     * Azon tectonok amelyeken keresztül halad a fonal
     */
    private List<Tecton> tectons;

    /**
     * Amely gombászhoz tartozik a fonal
     */
    private Mycologist mycologist;
    /**
     * Source fungus, ahonnan nő a fonal. 0. eleme a tecton listának
     */
    private Fungus fungus;

    private int lifespan = 4;

    public Hypha() {
        tectons = new ArrayList<>();
    }

    public Hypha(Mycologist m, Fungus f) {
        tectons = new ArrayList<>();
        mycologist = m; // Kinek a gombájáról
        fungus = f; // source fungus, ahonna indul a fonal
    }

    /**
     * Visszaadja, hogy mely gombászhoz tartozik a fonal
     * 
     * @return A fonalhoz tatozó gombász
     */
    public Mycologist getMycologist() {
        return mycologist;
    }

    /**
     * Beállítja, hogy mely gombászhoz tatozzon a fonal
     * 
     * @param species Gombász, akihez beállítódik a fonal
     */
    public void setMychologist(Mycologist m) {
        mycologist = m;
    }

    /**
     * Visszaadja, hogy mely gombához tartozik a fonal
     * 
     * @return A fonalhoz tatozó gomba
     */
    public Fungus getFungus() {
        return fungus;
    }

    /**
     * Beállítja, hogy mely gombához tatozzon a fonal
     * 
     * @param f Gomba, akihez beállítódik a fonal
     */
    public void setFungus(Fungus f) {
        fungus = f;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int i) {
        lifespan = i;
    }

    /**
     * Fonal haladásának tektonjai, konkrétan maga a fonal
     */
    public List<Tecton> getTectons() {
        return tectons;
    }

    private void setSpecies(Mycologist s) {
        this.mycologist = s;
    }

    public void setTectons(List<Tecton> t) {
        this.tectons = t;
    }

    public void connectTectons(Tecton... path) {
        Collections.addAll(tectons, path);
    }

    /**
     * Folytatja a már megkeztedd fonalat. Hozzáad "egy tectonnyi fonalat" a lista
     * végére
     */
    public boolean continueHypha(Tecton t) {
        // Ha ez az első tecton (pl. új fonalnál), engedjük
        if (tectons.isEmpty()) {
            tectons.add(t);
            return true;
        }

        // Ellenőrizzük, hogy az utolsó tecton szomszédja-e a cél
        Tecton last = tectons.get(tectons.size() - 1);
        if (last.isNeighbor(t)) {
            tectons.add(t);
            return true;
        }

        return false;
    }

    public boolean eatInsect(Insect i) {
        // Megnézzük, hogy a rovar rajta van-e az egyik olyan tektonon, amin fut a fonál
        Tecton targetTecton = null;
        for (Tecton t : tectons) {
            if (i.getTecton().equals(t) && i.isParalyzed()) {
                targetTecton = t;
                break;
            }
        }
        if (targetTecton == null) {
            return false;
        }

        // A rovart eltűntetjük a céltektonról, létrehozunk egy új gombát
        i.destroyInsect();
        mycologist.placeFungus(targetTecton);
        return true;
    }

    @Override
    public JsonObject serialize(ObjectNamer namer, Logger logger) {
        JsonObject obj = new JsonObject();

        // Gombász (determinált név)
        obj.addProperty("mycologist", namer.getNameOf(mycologist));

        // Gombatest (ha van hozzárendelve)
        if (fungus != null) {
            obj.addProperty("fungus", namer.getNameOf(fungus));
        }

        // Tectonok (névlista)
        obj.add("tectons", SerializerUtil.toJsonArray(tectons, namer::getNameOf));

        return obj;
    }

    public static Hypha deserialize(JsonObject obj, ObjectNamer namer, Logger logger) {
        Hypha h = new Hypha();

        // Mycologist visszakeresése
        String speciesName = obj.get("mycologist").getAsString();
        Object s = namer.getByName(speciesName);
        if (s instanceof Mycologist) {
            h.setSpecies((Mycologist) s);
        } else {
            logger.logError("Hypha", speciesName, "Ismeretlen vagy nem Mycologist típus");
        }

        // Fungus visszakeresése (ha szerepel)
        if (obj.has("fungus")) {
            String fungusName = obj.get("fungus").getAsString();
            Object f = namer.getByName(fungusName);
            if (f instanceof Fungus) {
                h.setFungus((Fungus) f);
            } else {
                logger.logError("Hypha", fungusName, "Ismeretlen vagy nem Fungus típus");
            }
        }

        // Tecton lista
        List<Tecton> tectonList = new ArrayList<>();
        JsonArray tArray = obj.getAsJsonArray("tectons");

        for (JsonElement e : tArray) {
            String tName = e.getAsString();
            Object t = namer.getByName(tName);
            if (t instanceof Tecton) {
                tectonList.add((Tecton) t);
            } else {
                logger.logError("Hypha", tName, "Ismeretlen vagy nem Tecton típus");
            }
        }

        h.setTectons(tectonList);
        return h;
    }
} // End of Hypha
