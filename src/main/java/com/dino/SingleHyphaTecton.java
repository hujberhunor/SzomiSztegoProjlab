package com.dino;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin egyszerre csak egy gombafonál lehet jelen.
 */
public class SingleHyphaTecton extends Tecton {
    /**
     * Konstruktor
     */
    public SingleHyphaTecton() {
        super();
        this.hyphaLimit = 1; // Garantáltan egy értékű konstans
    }
    
    /**
     * A fonalakat kezelő virtuális függvény felülírása.
     * 
     * @param h A kezelendő gombafonál
     */
    @Override
    protected void handleHypha(Hypha h) {
        // Implementáció később (Placeholder)
    }
}