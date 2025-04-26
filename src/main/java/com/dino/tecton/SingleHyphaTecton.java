package com.dino.tecton;

import com.dino.core.Hypha;
import com.dino.util.ObjectNamer;
import com.google.gson.JsonObject;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin egyszerre csak egy
 * gombafonál lehet jelen.
 */
public class SingleHyphaTecton extends Tecton {
    private int hyphaLimit;

    /**
     * Konstruktor
     */
    public SingleHyphaTecton() {
        super();
        hyphaLimit = 1; // Garantáltan egy értékű konstans
    }

    /**
     * A fonalakat kezelő virtuális függvény felülírása.
     * Ellenőrzi, hogy van-e már gombafonál a tektonon. Ha nincs,
     * hozzáadja az újat. Ha már van egy fonál, eltávolítja azt és
     * helyére teszi az újat.
     * 
     * @param h A kezelendő gombafonál
     */
    @Override
    public void handleHypha(Hypha h) {
        if (hyphas.isEmpty()) {
            // Ha még nincs fonál, egyszerűen hozzáadjuk az újat
            hyphas.add(h);
        } else {
            // Ha már van fonál, lecseréljük az újra
            Hypha existingHypha = hyphas.get(0);
            hyphas.clear();
            hyphas.add(h);

            // Itt lehetne kód, ami bejelenti a Game osztálynak, hogy egy fonál eltűnt
        }
    }

    /**
     * Visszaadja a tektonon megengedett fonalak számát
     * 
     * @return Mindig 1, mivel ez egy SingleHyphaTecton
     */
    public int getHyphaLimit() {
        return hyphaLimit;
    }

    /**
     * Létrehoz egy új, ugyanolyan típusú tektont
     * 
     * @return Új SingleHyphaTecton példány
     */
    @Override
    public Tecton createCopy() {
        return new SingleHyphaTecton();
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("hyphaLimit", hyphaLimit);

        return obj;
    }

     public void setHyphaLimit(int limit) {
        this.hyphaLimit = limit;
    }


}