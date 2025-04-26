package com.dino.tecton;

import com.dino.core.Hypha;
import com.dino.util.ObjectNamer;
import com.google.gson.JsonObject;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin a gombafonalak négy kör
 * leforgása után
 * maguktól elpusztulnak.
 */
public class ShortHyphaTecton extends Tecton {
    private int hyphaLifespan;
    private int hyphaLimit;

    /**
     * Konstruktor
     */
    public ShortHyphaTecton() {
        super();
        this.hyphaLifespan = 4; // Garantáltan négy értékű konstans
        this.hyphaLimit = 1; // Alapértelmezetten 1 fonál lehet rajta
    }

    /**
     * Csökkenti az összes rajta lévő fonál élettartamát
     */
    public void decreaseLifespan() {
        for (Hypha hypha : hyphas) {
            int currentLifespan = hypha.getLifespan();
            if (currentLifespan > 0) {
                hypha.setLifespan(currentLifespan - 1);

                // Ha lejárt az élettartam, jelezzük a Game osztálynak vagy
                // közvetlenül a tectonnak, hogy eltávolíthatja a listából
                if (hypha.getLifespan() == 0) {
                    // Ez a logika külső osztályban is lehet implementálva
                    // TODO: Ide jöhet a hypha eltávolítására vagy
                    // decayedHypha listához adására vonatkozó kód
                }
            }
        }
    }

    /**
     * A fonalakat kezelő virtuális függvény felülírása.
     * Ellenőrzi, hogy van-e már gombafonál a tektonon, és ha a limit engedi,
     * hozzáadja az újat. Beállítja a fonál élettartamát 4 körre.
     * 
     * @param h A kezelendő gombafonál
     */
    @Override
    public void handleHypha(Hypha h) {
        if (hyphas.size() < hyphaLimit || hyphaLimit == -1) {
            hyphas.add(h);
            // Minden új fonál élettartamát 4 körre állítjuk
            h.setLifespan(hyphaLifespan);
        }
    }

    /**
     * Visszaadja a tektonon lévő fonalak maximális élettartamát
     * 
     * @return A fonalak élettartama körökben mérve
     */
    public int getHyphaLifespan() {
        return hyphaLifespan;
    }

    /**
     * Létrehoz egy új, ugyanolyan típusú tektont
     * 
     * @return Új ShortHyphaTecton példány
     */
    @Override
    public Tecton createCopy() {
        return new ShortHyphaTecton();
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("hyphaLifespan", hyphaLifespan);
        obj.addProperty("hyphaLimit", hyphaLimit);

        return obj;
    }

}