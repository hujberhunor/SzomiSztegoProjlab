package com.dino.player;

import java.util.ArrayList;
import java.util.List;

import com.dino.core.Insect;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
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

    public Entomologist() {
        this.insects = new ArrayList<Insect>();
        this.actionsPerTurn = 2;
        this.remainingActions = 2;
    }

    public Entomologist(int actions) {
        this.insects = new ArrayList<>();
        this.actionsPerTurn = actions;
        this.remainingActions = actions;
    }

    /**
     * AcceleratingEffect miatt kell.
     * MIkor accel effect van rajta ez a roundban meg kell hívni
     */
    @Override
    public void increaseActions() {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        int prevActions = remainingActions;
        this.remainingActions++;

        logger.logChange("ENTOMOLOGIST", this, "REMAINING_ACTIONS",
                String.valueOf(prevActions), String.valueOf(this.remainingActions));
    }

    @Override
    public void decreaseActions() {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        int prevActions = remainingActions;
        if (remainingActions > 0) {
            remainingActions--;
            logger.logChange("ENTOMOLOGIST", this, "REMAINING_ACTIONS",
                    String.valueOf(prevActions), String.valueOf(this.remainingActions));
        } else {
            logger.logError("ENTOMOLOGIST", registry.getNameOf(this),
                    "Nincs több akció, nem csökkenthető.");
        }
    }
  
    public List<Insect> getInsects() { return insects; }
  
    public void addInsects(Insect insect) { insects.add(insect); }
}