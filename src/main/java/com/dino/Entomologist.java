package com.dino;

/**
 * Ez az osztály egy rovarászt reprezentál.
 * Összeköti a Player osztályt az Insect osztállyal.
 */
public class Entomologist extends Player {

    /**
     * Egy rovar példány, amit a rovarász irányít.
     */
    Insect insect;

    public Entomologist(int actions) {
        this.remainingActions = actions;
    }

    public void decreaseActions() {
        if (remainingActions > 0) {
            remainingActions--;
        }
    }
}
