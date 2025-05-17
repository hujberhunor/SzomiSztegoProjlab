package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class NextRoundCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        // Akciópontok nullázása
        game.getCurrentPlayer().remainingActions = 0;

        // Következő kör
        int result = game.nextRound();

        // Ha a játék véget ért (result = 0)
        if (result == 0) {
            game.endGame();
            logger.logChange("GAME", game, "STATE", "RUNNING", "ENDED");
            logger.logChange("GAME", game, "ROUND", "-", "Game ended");
        } else {
            logger.logChange("GAME", game, "ROUND", "-", "Advanced to next round");
        }
         game.notifyObservers();
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