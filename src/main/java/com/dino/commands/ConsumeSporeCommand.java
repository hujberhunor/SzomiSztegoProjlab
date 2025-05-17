package com.dino.commands;

import com.dino.core.Insect;
import com.dino.engine.Game;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class ConsumeSporeCommand implements Command {

    private final String insectName;

    public ConsumeSporeCommand(String insectName) {
        this.insectName = insectName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        Insect insect = (Insect) registry.getByName(insectName);
        String insectId = registry.getNameOf(insect);

        // Megpróbálja elfogyasztani a spórát
        boolean success = insect.consumeSpores(insect.getEntomologist());
        if (success) {
            logger.logChange("INSECT", insect, "CONSUME_SPORE", "-", "SUCCESS");
            insect.getEntomologist().decreaseActions();
        } else {
            logger.logError("INSECT", insectId, "Failed to consume spore.");
        }
         game.notifyObservers();
    }

    /**
     * Feladata, hogy validálja, hogy a user valid, helyes commandot adott meg
     * Csak az entitás létezését vizsgálja
     */
    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        return reg.getByName(insectName) instanceof Insect;
    }

    @Override
    public String toString() {
        return "CONSUME_SPORE " + insectName;
    }
}
