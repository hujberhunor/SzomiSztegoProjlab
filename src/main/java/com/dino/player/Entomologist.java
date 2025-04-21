package com.dino.player;

import com.dino.core.Insect;
import com.dino.util.Skeleton;

import java.util.List;

/**
 * Ez az osztály egy rovarászt reprezentál.
 * Összeköti a Player osztályt az Insect osztállyal.
 */
public class Entomologist extends Player {

    /**
     * Rovarok, amiket a rovarász irányít.
     */
    List<Insect> insects;

    public Entomologist(int actions) {
        this.remainingActions = actions;
    }

    public Entomologist() {
        this.remainingActions = 3;
    }

    /**
     * AcceleratingEffect miatt kell.
     * MIkor accel effect van rajta ez a roundban meg kell hívni
     */
    public void increaseActions() {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Entomologist", "increaseActions");
        int prevActions = remainingActions;

        this.remainingActions++;
        skeleton.log(
            "remainingActions növelve:" +
            prevActions +
            "-->" +
            this.remainingActions
        );

        skeleton.endMethod();
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

    public int getRemainingActions() {
        return this.remainingActions;
    }
    public void setActionsToZero() { remainingActions = 0; }

    public List<Insect> getInsects() { return insects; }
    public void addInsects(Insect insect) { insects.add(insect); }
}