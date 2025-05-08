package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class NextRoundCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        // Csak az akciókat nullázzuk le, a kör váltását a Game osztály kezeli
        game.getCurrentPlayer().remainingActions = 0;
        logger.logChange("GAME", game, "ROUND", "-", "Advanced to next round");
    }

    @Override
    public boolean validate(Game game) {
        return true;
    }

    @Override
    public String toString() {
        return "NEXT_ROUND";
    }
}