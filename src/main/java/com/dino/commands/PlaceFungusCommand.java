package com.dino.commands;

import com.dino.engine.Game;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class PlaceFungusCommand implements Command {

    private final String mycologistName;
    private final String tectonName;

    public PlaceFungusCommand(String mycologistName, String tectonName) {
        this.mycologistName = mycologistName;
        this.tectonName = tectonName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        Mycologist mycologist = (Mycologist) registry.getByName(mycologistName);
        Tecton tecton = (Tecton) registry.getByName(tectonName);
        String mycologistId = registry.getNameOf(mycologist);

        int prevFungiCount = mycologist.getMushrooms().size();
        mycologist.placeFungus(tecton);
        int newFungiCount = mycologist.getMushrooms().size();

        if (newFungiCount > prevFungiCount) {
            logger.logChange("MYCOLOGIST", mycologist, "PLACE_FUNGUS", tectonName, "SUCCESS");
            mycologist.decreaseActions();
        } else {
            logger.logError("MYCOLOGIST", mycologistId, "Failed to place fungus on tecton.");
        }
    }

    /**
     * Validálja, hogy a Mycologist és a Tecton objektum létezik
     */
    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        return reg.getByName(mycologistName) instanceof Mycologist
            && reg.getByName(tectonName) instanceof Tecton;
    }

    @Override
    public String toString() {
        return "PLACE_FUNGUS " + mycologistName + " " + tectonName;
    }
}
