package com.dino;

/**
 * EZ az osztály alapvető, minden játékos által használható cselekvéseket valósítja meg,
 * illetve egy olyan tulajdonságot tárol, ami mind gombászokra, mind rovarászokra egyaránt értelmezhető.
 */
public class Player {

    /**
     * A játékos neve.
     */
    protected String name;

    /**
     * Egy egész szám, ami a játékos jelenlegi pontszámát reprezentálja.
     */
    protected int score;

    /**
     *  Egy egész szám, ami megadja, hogy a játékos hány akcióval kezdi a körét.
     *  Értéke gombászok és rovarászok között eltérő.
     */
    protected int actionsPerTurn;

    /**
     * Egy egész szám, ami megadja, hogy a játékosnak hány akciója van hátra a körében.
     */
    protected int remainingActions;

    /**
     * Visszaad egy kivánt tektont.
     * @param tecton A kiválasztotandó tekton.
     * @return A kiválasztott tekton.
     */
    public Tecton selectTecton(Tecton tecton) {
        return tecton;
    }

    /**
     * Visszaadja a pontszám értékét.
     * @return A játékos aktuális pontszáma.
     */
    public int calculateScore() {
        return score;
    }
}
