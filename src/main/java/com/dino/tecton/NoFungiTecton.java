package com.dino.tecton;

import com.dino.core.Fungus;
import com.dino.core.Hypha;

import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

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
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);
        
        String hyphaName = registry.getNameOf(h);
        String tectonName = registry.getNameOf(this);
        
        if (hyphas.size() < hyphaLimit || hyphaLimit == -1) {
            hyphas.add(h);
            logger.logChange("TECTON", this, "ADD_HYPHA", "-", hyphaName);
        } else {
            logger.logError("TECTON", tectonName, "Nem lehet több gombafonalat hozzáadni: elérte a limitet");
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
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);
        
        String fungusName = registry.getNameOf(f);
        String tectonName = registry.getNameOf(this);
        
        // Nem engedjük a gombatestet ezen a típusú tektonon
        logger.logError("TECTON", tectonName, "Nem lehet gombatestet elhelyezni NoFungiTecton típusú tektonon");
        
        // Megjegyzés: nem hívjuk meg a super.setFungus(f) metódust, mert nem akarjuk, hogy gombatest legyen rajta
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