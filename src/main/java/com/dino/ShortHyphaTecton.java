package com.dino;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin a gombafonalak négy kör leforgása után
 * maguktól elpusztulnak.
 */
public class ShortHyphaTecton extends Tecton {
    private int hyphaLifespan;
    /**
     * Konstruktor
     */
    public ShortHyphaTecton() {
        super();
        this.hyphaLifespan = 4; // Garantáltan négy értékű konstans
    }

    public void decreaseLifespan(){
        if(hyphaLifespan > 0) hyphaLifespan--;
        // TODO else MÁR NEM LEHeT csökkenteni
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