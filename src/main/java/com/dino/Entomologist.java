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
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Entomologist", "decreaseActions");

        if (remainingActions > 0) {
            remainingActions--;
            skeleton.log("remainingActions csökkentve: " + remainingActions);
        } else {
            skeleton.log("Nincs több akció, nem csökkenthető.");
        }

        skeleton.endMethod();
    }
}
