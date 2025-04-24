package com.dino.tecton;

import com.dino.core.Hypha;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin egyszerre csak egy gombafonál lehet jelen.
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

    public int getHyphaLimit(){
        return hyphaLimit;
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
    
    @Override
    public Tecton createCopy() {
        return new SingleHyphaTecton();
    }
}