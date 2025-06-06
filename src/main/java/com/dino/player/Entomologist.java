package com.dino.player;

import java.util.ArrayList;
import java.util.List;

import com.dino.core.Insect;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.google.gson.JsonObject;

/**
 * Ez az osztály egy rovarászt reprezentál.
 * Összeköti a Player osztályt az Insect osztállyal.
 */
public class Entomologist extends Player implements SerializableEntity {

    /**
     * Rovarok, amiket a rovarász irányít.
     */
    List<Insect> insects;

    private static final ObjectNamer namer = ObjectNamer.getInstance();

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
        Logger logger = Logger.getInstance();

        int prevActions = remainingActions;
        this.remainingActions++;

        logger.logChange("ENTOMOLOGIST", this, "REMAINING_ACTIONS",
                String.valueOf(prevActions), String.valueOf(this.remainingActions));
    }

    @Override
    public void decreaseActions() {
        Logger logger = Logger.getInstance();

        int prevActions = remainingActions;
        if (remainingActions > 0) {
            remainingActions--;
            logger.logChange("ENTOMOLOGIST", this, "REMAINING_ACTIONS",
                    String.valueOf(prevActions), String.valueOf(this.remainingActions));
        } else {
            logger.logError("ENTOMOLOGIST", namer.getName(this),
                    "Nincs több akció, nem csökkenthető.");
        }
    }

    public int getRemainingActions() {
        return this.remainingActions;
    }

    public void setActions(int i) {
        remainingActions = i;
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", namer.getName(this));
        obj.addProperty("type", "Entomologist");

        obj.addProperty("score", this.score);
        obj.addProperty("remainingActions", this.remainingActions);

        // Entomologist specifikus adatok (pl. irányított rovarok listája)
        obj.add("insects", SerializerUtil.toJsonArray(
                getInsects(), // kell hozzá egy getInsects() getter a saját rovarjaidra
                namer::getName));

        return obj;
    }
  
    public List<Insect> getInsects() { return insects; }
  
    public void addInsects(Insect insect) { insects.add(insect); }
}

