package com.dino.tecton;

import com.dino.core.Hypha;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin a gombafonalak korlátlan számban
 * lehetnek jelen.
 */
public class InfiniteHyphaTecton extends Tecton {

    /// Attribútum
    private int hyphaCount; // A tektonon lévő fonalak száma
    private int hyphaLimit; // Korlátlan (-1)
    
    /**
     * Konstruktor
     */
    public InfiniteHyphaTecton() {
        super();
        this.hyphaLimit = -1; // Végtelen számú fonál biztosítása
        this.hyphaCount = 0;
    }

    /**
     * A fonalakat kezelő virtuális függvény felülírása.
     * Elfogad bármilyen gombafonalat, mivel korlátlan számú fonál lehet rajta.
     *
     * @param h A kezelendő gombafonál
     */
    @Override
    public void handleHypha(Hypha h) {
    EntityRegistry registry = new EntityRegistry();
    Logger logger = new Logger(registry);
    
    String hyphaName = registry.getNameOf(h);
    String tectonName = registry.getNameOf(this);
    
    // Minden fonalat elfogadunk, mivel korlátlan számú lehet
    hyphas.add(h);
    hyphaCount++;
    
    logger.logChange("TECTON", this, "ADD_HYPHA", String.valueOf(hyphaCount-1), String.valueOf(hyphaCount));
}

    /**
     * Hyphacount növelése
     */
    public void increaseHyphaCount() {
        this.hyphaCount++;
    }
   
    /**
     * Hyphacount csökkentése
     */
    public void decreaseHyphaCount() {
        if (this.hyphaCount > 0) {
            this.hyphaCount--;
        }
    }

    /**
     * Hyphacount getter
     *
     * @return A tektonon lévő fonalak száma
     */
    public int getHyphaCount() {
        return hyphaCount;
    }
    
    /**
     * Létrehoz egy új, ugyanolyan típusú tektont
     * 
     * @return Új InfiniteHyphaTecton példány
     */
    @Override
    public Tecton createCopy() {
        return new InfiniteHyphaTecton();
    }
}