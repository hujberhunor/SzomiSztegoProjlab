package com.dino;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin a gombafonalak négy kör leforgása után
 * maguktól elpusztulnak.
 */
public class ShortHyphaTecton extends Tecton {
    /**
     * Konstruktor
     */
    public ShortHyphaTecton() {
        super();
        this.hyphaLifespan = 4; // Garantáltan négy értékű konstans
    }
    
    /**
     * A fonalakat kezelő virtuális függvény felülírása.
     * 
     * @param h A kezelendő gombafonál
     */
    @Override
    protected void handleHypha(Hypha h) {
        //TODO
    }
}