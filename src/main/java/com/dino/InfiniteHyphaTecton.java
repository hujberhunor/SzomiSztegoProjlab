package com.dino;

/**
 * Egy olyan konkrét, példányosítható tektontípus, amin a gombafonalak korlátlan számban
 * lehetnek jelen.
 */
public class InfiniteHyphaTecton extends Tecton {

    /// Attribútum
    private int hyphaCount; /// A tektonon lévő fonalak száma == hpyhas.size()
    private int hyphaLimit;
    
    /**
     * Konstruktor
     */
    public InfiniteHyphaTecton() {
        super();
        this.hyphaLimit = -1; // Végtelen számú fonál biztosítása
        this.hyphaCount = hyphas.size();
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

    /**
     * Hyphacount növelése
     */
    public void increaseHyphaCount() {
        this.hyphaCount++;
    }
   
    public void decreaseHyphaCount() {
        this.hyphaCount--;
    }

    /**
     * Hyphacount getter
     *
     * @return A tektonon lévő fonalak száma
     */
    public int getHyphaCount() {
        return hyphaCount;
    }
}
