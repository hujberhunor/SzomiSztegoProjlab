package com.dino;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin nem nőhetnek gombatestek.
 */
public class NoFungiTecton extends Tecton {
    /**
     * Konstruktor
     */
    public NoFungiTecton() {
        super();
        this.fungiEnabled = false; // Garantáltan hamis értékű konstans
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