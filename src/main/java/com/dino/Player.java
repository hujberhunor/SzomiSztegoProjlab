package com.dino;

/**
 * EZ az osztály alapvető, minden játékos által használható cselekvéseket valósítja meg,
 * illetve egy olyan tulajdonságot tárol, ami mind gombászokra, mind rovarászokra egyaránt értelmezhető.
 */
public class Player {
    /**
     * A játékos neve.
     */
    private String name;

    /**
     * Egy egész szám, ami a játékos jelenlegi pontszámát reprezentálja.
     */
    private int score;

    /**
     *  Egy egész szám, ami megadja, hogy a játékos hány akcióval kezdi a körét.
     *  Értéke gombászok és rovarászok között eltérő.
     */
    private int actionsPerTurn;

    /**
     * Egy egész szám, ami megadja, hogy a játékosnak hány akciója van hátra a körében.
     */
    private int remainingActions;

    /**
     * Amolyan szubrutin amelyet szinte minden akció előtt meg kell hívni.
     * @param tecton A kiválasztott tekton.
     * @return
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
