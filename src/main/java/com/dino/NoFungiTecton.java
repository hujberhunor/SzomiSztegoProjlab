package com.dino;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin nem nőhetnek gombatestek.
 */
public class NoFungiTecton extends Tecton {
    private boolean fungiEnabled;
    /**
     * Konstruktor
     */
    public NoFungiTecton() {
        super();
        fungiEnabled = false; // Garantáltan hamis értékű konstans
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