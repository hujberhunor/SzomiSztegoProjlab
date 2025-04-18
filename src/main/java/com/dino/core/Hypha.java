package com.dino.core;

import java.util.ArrayList;
import java.util.List;

import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.google.gson.JsonObject;


/**
 * A gombafonalakat reprezentáló osztály. Egy objektum a növesztés sorrendjében tartalmazza a tektonokat,
 * amiken keresztül nő, hogy a fonál elszakadása esetén (rovar vagy törés hatására) a szekvenciából egyértelmű
 * legyen, hogy a fonál melyik fele nem kapcsolódik már a gombatestből, amiből származnak.
 */
public class Hypha implements SerializableEntity {

    /**
     *  Azon tectonok amelyeken keresztül halad a fonal
     */
    private List<Tecton> tectons;

    /**
     *  Amely gombászhoz tartozik a fonal
     */
    private Mycologist mycologist;
    /**
     * Source fungus, ahonnan nő a fonal. 0. eleme a tecton listának
     */
    private Fungus fungus;

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
     * @return A fonalhoz tatozó gombász
     */
    public Mycologist getMycologist() {
        return mycologist;
    }

    /**
     * Beállítja, hogy mely gombászhoz tatozzon a fonal
     * @param species Gombász, akihez beállítódik a fonal
     */
    public void setMychologist(Mycologist m) {
        this.mycologist = m;
    }

    /**
     * Folytatja a már megkeztedd fonalat. Hozzáad "egy tectonnyi fonalat" a lista végére
     */
    public void continueHypha(Tecton t) {
        tectons.add(t);
    }

    /**
     * Fonal haladásának tektonja, konkrétan maga a fonal
     */
    public List<Tecton> getTectons() {
        return tectons;
    }

    public void connectTectons(Tecton... path) {
        for (Tecton t : path) {
            tectons.add(t);
        }
    }

    @Override
    public JsonObject serialize() {
        JsonObject obj = new JsonObject();

        // Kihez tartozik a hypha (gombász ID)
        obj.addProperty("mycologist", "mycologist_" + mycologist.hashCode());

        // Mely tectonokon halad át (tecton ID lista)
        obj.add("tectons", SerializerUtil.toJsonArray(tectons, t -> "tecton_" + t.hashCode()));

        return obj;
    }
} // End of Hypha
