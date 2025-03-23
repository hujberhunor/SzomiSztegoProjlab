package com.dino;

import java.util.ArrayList;
import java.util.List;

/**
 * A gombafonalakat reprezentáló osztály. Egy objektum a növesztés sorrendjében tartalmazza a tektonokat,
 * amiken keresztül nő, hogy a fonál elszakadása esetén (rovar vagy törés hatására) a szekvenciából egyértelmű
 * legyen, hogy a fonál melyik fele nem kapcsolódik már a gombatestből, amiből származnak.
 */
public class Hypha {

    /**
     *  Azon tectonok amelyeken keresztül halad a fonal
     */
    private List<Tecton> tectons = new ArrayList<>();

    /**
     *  Amely gombászhoz tartozik a fonal
     */
    private Mycologist species;

    /**
     * Visszaadja, hogy mely gombászhoz tartozik a fonal
     * @return A fonalhoz tatozó gombász
     */
    public Mycologist getSpecies() {
        return species;
    }

    /**
     * Beállítja, hogy mely gombászhoz tatozzon a fonal
     * @param species Gombász, akihez beállítódik a fonal
     */
    public void setSpecies(Mycologist species) {
        this.species = species;
    }

    /**
     *  Visszaadja, hogy van-e kapcsolat gombatesthez, ha nincs akkor elhalt a fonal
     */
    public boolean isConnectedToFungus(Hypha h) {
        // temp has
        return false;
    }

    /**
     * Folytatja a már megkeztedd fonalat. Hozzáad "egy tectonnyi fonalat"
     */
    public void continueHypha(Tecton t) {}

    public void continueHypha(Hypha h) {}

    public List<Tecton> getTectons(){
        return tectons;
    }
} // End of Hypha