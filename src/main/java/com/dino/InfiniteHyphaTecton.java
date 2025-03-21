package com.dino;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin a gombafonalak korlátlan számban
 * lehetnek jelen.
 */
public class InfiniteHyphaTecton extends Tecton {
    // Attribútumok
    private int hyphaCount; // A tektonon lévő fonalak száma
    
    /**
     * Konstruktor
     */
    public InfiniteHyphaTecton() {
        super();
        this.hyphaLimit = -1; // Garantáltan mínusz egy értékű konstans (végtelen)
        this.hyphaCount = 0;
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
    
    /**
     * Hyphacount getter->CLAUDE ORIGINAL
     * 
     * @return A tektonon lévő fonalak száma
     */
    public int getHyphaCount() {
        return hyphaCount;
    }
    
    /**
     * Hyphacount növelése->CLAUDE ORIGINAL
     */
    public void incrementHyphaCount() {
        this.hyphaCount++;
    }
}
