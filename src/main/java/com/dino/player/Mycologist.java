package com.dino.player;

import java.util.ArrayList;
import java.util.List;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.google.gson.JsonObject;

/**
 * Ez az osztály egy gombászt reprezentál.
 * Megvalósítja a gombászokra specifikus olyan akciót, ami nem egy specifikus
 * gombához tartozik,
 * hanem magához a játékoshoz, illetve számontartja a játékos gombatestjeit.
 */
public class Mycologist extends Player implements SerializableEntity {

    /**
     * Egy lista, ami a gombász által vezérelt gombatesteket tárolja.
     */
    List<Fungus> mushrooms;

    private static final ObjectNamer namer = ObjectNamer.getInstance();

    public Mycologist() {
        this.mushrooms = new ArrayList<Fungus>();
        this.actionsPerTurn = 1;
        this.remainingActions = 1;
    }

    public Mycologist(int actions) {
        this.mushrooms = new ArrayList<Fungus>();
        this.actionsPerTurn = actions;
        this.remainingActions = actions;
    }

    /**
     * Elhelyez egy új gombatestet a paraméterként átadott tektonon,
     * ha azon van a gombász által vezérelt fajnak fonala és kellő mennyiségű
     * spórája.
     * 
     * @param t Ezen a tektonon lesz elhelyezve a gomatest.
     */
    public void placeFungus(Tecton t) {
        Logger logger = Logger.getInstance();

        String tectonName = namer.getName(t);
        
        // Ellenőrizzük, hogy a tekton tartalmaz-e a gombász által vezérelt fajnak
        // megfelelő fonalat
        boolean tectonHasHypha = false;
        for (Hypha h : t.getHyphas()) {
            if (h.getMycologist().equals(this)) {
                tectonHasHypha = true;
            }
        }
        if (!tectonHasHypha) {
            logger.logError("MYCOLOGIST", namer.getName(this),
                    "Nem lehet elhelyezni a gombát: nincs megfelelő gombafonál a tektonon " + tectonName);
            return;
        }

        // Ellenőrizzük, hogy van-e elegendő spóra
        if (!t.hasSpores(this)) {
            logger.logError("MYCOLOGIST", namer.getName(this),
                    "Nem lehet elhelyezni a gombát: nincs elegendő spóra a tektonon " + tectonName);
            return;
        }
        
        int oldMushroomsCount = mushrooms.size();

        // Gombatest létrehozása és hozzáadása a listához
        Fungus newFungus = new Fungus(this, t);
        mushrooms.add(newFungus);
        t.setFungus(newFungus);
        this.increaseScore(1);

        logger.logChange("MYCOLOGIST", this, "MUSHROOMS_COUNT",
                String.valueOf(oldMushroomsCount), String.valueOf(mushrooms.size()));
        logger.logOk("MYCOLOGIST", namer.getName(this),
                "ACTION", "ATTEMPT_PLACE_FUNGUS", "SUCCESS");
    }

    public List<Fungus> getMushrooms() {
        return mushrooms;
    }

    @Override
    public void increaseActions() {
        Logger logger = Logger.getInstance();

        int prevActions = remainingActions;
        this.remainingActions++;

        logger.logChange("MYCOLOGIST", this, "REMAINING_ACTIONS",
                String.valueOf(prevActions), String.valueOf(this.remainingActions));
    }

    @Override
    public void decreaseActions() {
        Logger logger = Logger.getInstance();

        int prevActions = remainingActions;
        if (remainingActions > 0) {
            remainingActions--;
            logger.logChange("MYCOLOGIST", this, "REMAINING_ACTIONS",
                    String.valueOf(prevActions), String.valueOf(this.remainingActions));
        } else {
            logger.logError("MYCOLOGIST", namer.getName(this),
                    "Nincs több akció, nem csökkenthető.");
        }
    }

    /**
     * Gomba lehelyezése gombafonál nélkül.
     * Első gomba lehelyezéséhez
     */
    public void debugPlaceFungus(Tecton t){
        Fungus f = new Fungus(this, t);
        namer.register(f);
        t.setFungus(f);
        mushrooms.add(f);
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", namer.getName(this));
        obj.addProperty("type", "Mycologist");

        // Player mezők
        obj.addProperty("score", this.score);
        obj.addProperty("remainingActions", this.remainingActions);

        // Gombatestek név szerinti felsorolása
        obj.add("mushrooms", SerializerUtil.toJsonArray(
                mushrooms,
                namer::getName));

        return obj;
    }

}