package com.dino.player;

import java.util.List;

import com.dino.core.Insect;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.Skeleton;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Ez az osztály egy rovarászt reprezentál.
 * Összeköti a Player osztályt az Insect osztállyal.
 */
public class Entomologist extends Player implements SerializableEntity{

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
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        int prevActions = remainingActions;
        this.remainingActions++;

        logger.logChange("ENTOMOLOGIST", this, "REMAINING_ACTIONS",
                String.valueOf(prevActions), String.valueOf(this.remainingActions));
    }

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

    public int getRemainingActions() {
        return this.remainingActions;
    }
  
    public void setActions(int i) { remainingActions = i; }
  
    public List<Insect> getInsects() { return insects; }
  
    public void addInsects(Insect insect) { insects.add(insect); }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'serialize'");
    }

}

