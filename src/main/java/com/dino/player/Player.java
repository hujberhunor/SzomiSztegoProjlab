package com.dino.player;

import com.dino.tecton.Tecton;
import com.google.gson.JsonObject;

/**
 * Ez az osztály alapvető, minden játékos által használható cselekvéseket
 * valósítja meg,
 * illetve egy olyan tulajdonságot tárol, ami mind gombászokra, mind
 * rovarászokra egyaránt értelmezhető.
 */
public abstract class Player {
    /**
     * A játékos neve.
     */

    /**
     * Egy egész szám, ami a játékos jelenlegi pontszámát reprezentálja.
     */
    public int score;

    /**
     * Egy egész szám, ami megadja, hogy a játékos hány akcióval kezdi a körét.
     * Értéke gombászok és rovarászok között eltérő.
     */
    public int actionsPerTurn;

    /**
     * Egy egész szám, ami megadja, hogy a játékosnak hány akciója van hátra a
     * körében.
     */
    public int remainingActions;

    public Player() {
        this.score = 0;
        this.actionsPerTurn = 3; // alapértelmezett érték, pl. rovarásznak
        this.remainingActions = 3;
    }

    /**
     * Visszaad egy kivánt tektont.
     * 
     * @param tecton A kiválasztotandó tekton.
     * @return A kiválasztott tekton.
     */
    public Tecton selectTecton(Tecton tecton) {
        return tecton;
    }

    /**
     * Visszaadja a pontszám értékét.
     * 
     * @return A játékos aktuális pontszáma.
     */
    public int calculateScore() {
        return score;
    }

    public JsonObject serialize() {
        JsonObject obj = new JsonObject();
        obj.addProperty("score", this.score);
        obj.addProperty("actionsPerTurn", this.actionsPerTurn);
        obj.addProperty("remainingActions", this.remainingActions);
        return obj;
    }

}