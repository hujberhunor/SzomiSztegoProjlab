package com.dino.player;

import com.dino.tecton.Tecton;

/**
 * Ez az osztály alapvető, minden játékos által használható cselekvéseket valósítja meg,
 * illetve egy olyan tulajdonságot tárol, ami mind gombászokra, mind rovarászokra egyaránt értelmezhető.
 */
public abstract class Player {
    /**
     * A játékos neve.
     */
    public String name;

    /**
     * Egy egész szám, ami a játékos jelenlegi pontszámát reprezentálja.
     */
    public int score;

    /**
     *  Egy egész szám, ami megadja, hogy a játékos hány akcióval kezdi a körét.
     *  Értéke gombászok és rovarászok között eltérő.
     */
    public int actionsPerTurn;

    /**
     * Egy egész szám, ami megadja, hogy a játékosnak hány akciója van hátra a körében.
     */
    public int remainingActions;

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