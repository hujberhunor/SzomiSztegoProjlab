package com.dino.tecton;

import com.dino.core.Hypha;
import com.dino.engine.Game;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

import com.dino.util.ObjectNamer;
import com.google.gson.JsonObject;
import java.util.List;

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
        ObjectNamer namer = ObjectNamer.getInstance();
        EntityRegistry registry = EntityRegistry.getInstance();
        Logger logger = Logger.getInstance();
        Game game = Game.getInstance();
        
        for (Hypha hypha : hyphas) {
            int currentLifespan = hypha.getLifespan();
            if (currentLifespan > 0) {
                // Csökkentjük az élettartamot
                hypha.setLifespan(currentLifespan - 1);

                
                // Logoljuk az élettartam változását
                logger.logChange("HYPHA", hypha, "LIFESPAN", String.valueOf(currentLifespan), 
                                String.valueOf(hypha.getLifespan()));
                
                // Ha lejárt az élettartam

                // Ha lejárt az élettartam, jelezzük a Game osztálynak vagy
                // közvetlenül a tectonnak, hogy eltávolíthatja a listából
                if (hypha.getLifespan() == 0) {
                    hypha.splitHypha(this, game);
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
        EntityRegistry registry = EntityRegistry.getInstance();
        Logger logger = Logger.getInstance();
        
        String hyphaName = registry.getNameOf(h);
        String tectonName = registry.getNameOf(this);
        
        if (hyphas.size() < hyphaLimit || hyphaLimit == -1) {
            // Eredeti élettartam mentése
            int originalLifespan = h.getLifespan();
            
            // Hozzáadjuk a hyphas listához
            hyphas.add(h);
            
            // Beállítjuk az élettartamot 4 körre
            h.setLifespan(hyphaLifespan);
            
            // Logoljuk a változásokat
            logger.logChange("TECTON", this, "ADD_HYPHA", "-", hyphaName);
            logger.logChange("HYPHA", h, "LIFESPAN", String.valueOf(originalLifespan), String.valueOf(hyphaLifespan));
        } else {
            logger.logError("TECTON", tectonName, "Nem lehet több gombafonalat hozzáadni: elérte a limitet");
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

    public void setHyphaLimit(int limit) {
        this.hyphaLimit = limit;
    }

    public void setHyphaLifespan(int lifespan) {
        this.hyphaLifespan = lifespan;
    }

}