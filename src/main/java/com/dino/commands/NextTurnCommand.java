package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class NextTurnCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        // Jelenlegi játékos akcióinak nullázása
        game.getCurrentPlayer().remainingActions = 0;
        logger.logChange("GAME", game, "TURN", "-", "Turn ended");
    }

    @Override
    public boolean validate(Game game) {
        return true;
    }

    @Override
    public String toString() {
        return "NEXT_TURN";
    }
}