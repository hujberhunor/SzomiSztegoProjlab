package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class EndGameCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        game.endGame();
        logger.logChange("GAME", game, "END", "-", "Game ended");
    }

    /**
     * Mindig érvényes, mert nem igényel paramétert
     */
    @Override
    public boolean validate(Game game) {
        return true;
    }

    @Override
    public String toString() {
        return "END_GAME";
    }
}

