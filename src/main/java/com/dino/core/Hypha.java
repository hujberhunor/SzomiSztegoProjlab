package com.dino.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
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
     * Folytatja a már megkeztedd fonalat. Hozzáad "egy tectonnyi fonalat" a lista
     * végére
     */
    public boolean continueHypha(Tecton t) {
    // Ha ez az első tecton (pl. új fonalnál), engedjük
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        if (tectons.isEmpty()) {
            tectons.add(t);
            logger.logOk("HYPHA", registry.getNameOf(this),"ACTION", "ATTEMPT_CONTINUE_HYPA", "SUCCESS");

            return true;
        }

        // Ellenőrizzük, hogy az utolsó tecton szomszédja-e a cél
        Tecton last = tectons.get(tectons.size() - 1);
        if (last.isNeighbor(t)) {
            tectons.add(t);
            logger.logOk("HYPHA", registry.getNameOf(this),"ACTION", "ATTEMPT_CONTINUE_HYPA", "SUCCESS");
            return true;
        }

        logger.logError("HYPHA", registry.getNameOf(this), "A fonálnövesztés sikertelen.");
        return false;
    }

    /**
     * Fonal haladásának tektonjai, konkrétan maga a fonal
     */
    public List<Tecton> getTectons() {
        return tectons;
    }

    public void connectTectons(Tecton... path) {
        Collections.addAll(tectons, path);
    }

    public boolean eatInsect(Insect i){
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        // Megnézzük, hogy a rovar rajta van-e az egyik olyan tektonon, amin fut a fonál
        Tecton targetTecton = null;
        for (Tecton t : tectons) {
            if (i.getTecton().equals(t) && i.isUnderEffect(3)) {
                targetTecton = t;
                break;
            }
        }
      
        if (targetTecton == null){
            logger.logError("HYPHA", registry.getNameOf(this), "A rovar nem olyan tektonon van, amin fut fonál.");
            return false;
        }

        // A rovart eltűntetjük a céltektonról, létrehozunk egy új gombát
        i.destroyInsect();
        mycologist.placeFungus(targetTecton);
        logger.logOk("HYPHA", registry.getNameOf(this),"ACTION", "ATTEMPT_EAT_INSECT", "SUCCESS");
        return true;
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", namer.getName(this));
        obj.addProperty("type", "Hypha");

        obj.addProperty("mycologist", namer.getName(mycologist));
        obj.addProperty("fungus", namer.getName(fungus));
        obj.addProperty("lifespan", lifespan);

        obj.add("tectons", SerializerUtil.toJsonArray(
                tectons,
                namer::getName));

        return obj;
    }
} // End of Hypha
