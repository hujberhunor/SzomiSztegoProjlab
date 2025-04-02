package com.dino.tecton;
import com.dino.core.Hypha;

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
    public void handleHypha(Hypha h) {
        //TODO
    }
}