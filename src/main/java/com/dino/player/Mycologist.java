package com.dino.player;

import java.util.ArrayList;
import java.util.List;

import com.dino.core.Hypha;
import com.dino.core.Fungus;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.Skeleton;

/**
 * Ez az osztály egy gombászt reprezentál.
 * Megvalósítja a gombászokra specifikus olyan akciót, ami nem egy specifikus gombához tartozik,
 * hanem magához a játékoshoz, illetve számontartja a játékos gombatestjeit.
 */
public class Mycologist extends Player {

    /**
     * Egy lista, ami a gombász által vezérelt gombatesteket tárolja.
     */
    List<Fungus> mushrooms;

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
     * ha azon van a gombász által vezérelt fajnak fonala és kellő mennyiségű spórája.
     * @param t Ezen a tektonon lesz elhelyezve a gomatest.
     */
    public void placeFungus(Tecton t) {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        String tectonName = registry.getNameOf(t);

        // Ellenőrizzük, hogy a tekton tartalmaz-e a gombász által vezérelt fajnak megfelelő fonalat
        boolean tectonHasHypha = false;
        for (Hypha h : t.getHyphas()) {
            if (h.getMycologist().equals(this)) {
                tectonHasHypha = true;
            }
        }
        if (!tectonHasHypha) {
            logger.logError("MYCOLOGIST", registry.getNameOf(this),
                    "Nem lehet elhelyezni a gombát: nincs megfelelő gombafonál a tektonon " + tectonName);
            return;
        }

        // Ellenőrizzük, hogy van-e elegendő spóra
        if (!t.hasSpores(this)) {
            logger.logError("MYCOLOGIST", registry.getNameOf(this),
                    "Nem lehet elhelyezni a gombát: nincs elegendő spóra a tektonon " + tectonName);
            return;
        }

        int oldMushroomsCount = mushrooms.size();

        // Gombatest létrehozása és hozzáadása a listához
        Fungus newFungus = new Fungus(this, t);
        mushrooms.add(newFungus);
        t.setFungus(newFungus);

        logger.logChange("MYCOLOGIST", this, "MUSHROOMS_COUNT",
                String.valueOf(oldMushroomsCount), String.valueOf(mushrooms.size()));
        logger.logOk("MYCOLOGIST", registry.getNameOf(this),
                "ACTION", "ATTEMPT_PLACE_FUNGUS", "SUCCESS");
    }

    public List<Fungus> getMushrooms(){
        return mushrooms;
    }

    @Override
    public void increaseActions() {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        int prevActions = remainingActions;
        this.remainingActions++;

        logger.logChange("MYCOLOGIST", this, "REMAINING_ACTIONS",
                String.valueOf(prevActions), String.valueOf(this.remainingActions));
    }

    @Override
    public void decreaseActions() {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        int prevActions = remainingActions;
        if (remainingActions > 0) {
            remainingActions--;
            logger.logChange("MYCOLOGIST", this, "REMAINING_ACTIONS",
                    String.valueOf(prevActions), String.valueOf(this.remainingActions));
        } else {
            logger.logError("MYCOLOGIST", registry.getNameOf(this),
                    "Nincs több akció, nem csökkenthető.");
        }
    }

    /**
     * Gomba lehelyezése gombafonál nélkül.
     * Első gomba lehelyezéséhez
     */
    public void debugPlaceFungus(Tecton t){
        Fungus f = new Fungus();
        t.setFungus(f);
    }
}