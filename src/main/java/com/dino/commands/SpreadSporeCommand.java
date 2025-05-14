package com.dino.commands;

import com.dino.core.Fungus;
import com.dino.engine.Game;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class SpreadSporeCommand implements Command {

    private final String fungusName;

    public SpreadSporeCommand(String fungusName) {
        this.fungusName = fungusName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        Fungus fungus = (Fungus) registry.getByName(fungusName);
        String fungusId = registry.getNameOf(fungus);

        int prevCharge = fungus.getCharge();
        fungus.spreadSpores();
        logger.logChange("FUNGUS", fungus, "SPREAD_SPORE", "CHARGE=" + prevCharge, "CHARGE=0");
        fungus.getSpecies().decreaseActions();
    }

    /**
     * Validálja, hogy létezik-e a megadott Fungus objektum
     */
    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        return reg.getByName(fungusName) instanceof Fungus;
    }

    @Override
    public String toString() {
        return "SPREAD_SPORE " + fungusName;
    }
}
