package com.dino.tecton;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.util.ObjectNamer;
import com.google.gson.JsonObject;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin nem nőhetnek
 * gombatestek.
 */
public class NoFungiTecton extends Tecton {
    private boolean fungiEnabled;
    private int hyphaLimit;

    /**
     * Konstruktor
     */
    public NoFungiTecton() {
        super();
        fungiEnabled = false; // Garantáltan hamis értékű konstans
        hyphaLimit = 1; // Alapértelmezetten 1 fonál lehet rajta
    }

    /**
     * A fonalakat kezelő virtuális függvény felülírása.
     * Ellenőrzi, hogy van-e már gombafonál a tektonon, és ha a limit engedi,
     * hozzáadja az újat.
     * 
     * @param h A kezelendő gombafonál
     */
    @Override
    public void handleHypha(Hypha h) {
        if (hyphas.size() < hyphaLimit || hyphaLimit == -1) {
            hyphas.add(h);
        }
    }

    /**
     * Felülírja a setFungus metódust, hogy megakadályozza a gombatestek növekedését
     * ezen a tektonon.
     * 
     * @param f A gombatest, amit meg próbálnánk helyezni a tektonon
     */
    @Override
    public void setFungus(Fungus f) {
        // Nem tesz semmit, mivel ezen a típusú tektonon nem nőhet gombatest
        // Az alaposztály metódusa felül van írva, és nem hívjuk meg
    }

    /**
     * Ellenőrzi, hogy ezen a tektonon lehet-e gombatestet növeszteni.
     * 
     * @return Mindig hamis, mivel ezen a típusú tektonon nem nőhet gombatest
     */
    public boolean isFungiEnabled() {
        return fungiEnabled; // Mindig false
    }

    /**
     * Létrehoz egy új, ugyanolyan típusú tektont
     * 
     * @return Új NoFungiTecton példány
     */
    @Override
    public Tecton createCopy() {
        return new NoFungiTecton();
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("fungiEnabled", fungiEnabled);
        obj.addProperty("hyphaLimit", hyphaLimit);

        return obj;
    }

}