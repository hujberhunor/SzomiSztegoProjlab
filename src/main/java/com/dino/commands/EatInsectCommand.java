package com.dino.commands;

import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.engine.Game;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class EatInsectCommand implements Command {

    private final String hyphaName;
    private final String insectName;

    public EatInsectCommand(String hyphaName, String insectName) {
        this.hyphaName = hyphaName;
        this.insectName = insectName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        Hypha hypha = (Hypha) registry.getByName(hyphaName);
        Insect insect = (Insect) registry.getByName(insectName);
        String hyphaId = registry.getNameOf(hypha);
        String insectId = registry.getNameOf(insect);

        boolean success = hypha.eatInsect(insect);
        if (success) {
            logger.logChange("HYPHA", hypha, "EAT_INSECT", insectId, "SUCCESS");
            hypha.getMycologist().decreaseActions();
        } else {
            logger.logError("HYPHA", hyphaId, "Failed to eat insect: not on hypha.");
        }
         game.notifyObservers();
    }

    /**
     * Validálja, hogy létezik-e a megadott Hypha és Insect objektum
     */
    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        return reg.getByName(hyphaName) instanceof Hypha
            && reg.getByName(insectName) instanceof Insect;
    }

    @Override
    public String toString() {
        return "EAT_INSECT " + hyphaName + " " + insectName;
    }
}
