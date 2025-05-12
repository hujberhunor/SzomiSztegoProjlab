package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class SkipTurnCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        // Csak az akciókat nullázzuk le
        game.getCurrentPlayer().remainingActions = 0;
        logger.logChange("GAME", game, "TURN", "-", "Skipped to next turn");
    }

    @Override
    public boolean validate(Game game) {
        return true;
    }

    @Override
    public String toString() {
        return "SKIP_TURN";
    }
}