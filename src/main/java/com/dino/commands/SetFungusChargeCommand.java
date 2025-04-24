package com.dino.commands;

import com.dino.core.Fungus;
import com.dino.engine.Game;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class SetFungusChargeCommand implements Command {

    private final String fungusName;
    private final int charge;

    public SetFungusChargeCommand(String fungusName, int charge) {
        this.fungusName = fungusName;
        this.charge = charge;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        Fungus fungus = (Fungus) registry.getByName(fungusName);
        String fungusId = registry.getNameOf(fungus);

        int prevCharge = fungus.getCharge();
        fungus.setCharge(charge);

        logger.logChange("FUNGUS", fungus, "CHARGE", String.valueOf(prevCharge), String.valueOf(charge));
    }

    /**
     * Validálja, hogy a megadott objektum létezik-e, és a charge érték nem negatív
     */
    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        return reg.getByName(fungusName) instanceof Fungus && charge >= 0;
    }

    @Override
    public String toString() {
        return "SET_FUNGUS_CHARGE " + fungusName + " " + charge;
    }
}
