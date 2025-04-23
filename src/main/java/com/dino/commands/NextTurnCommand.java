package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class NextTurnCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        game.nextTurn();
        logger.logChange("GAME", game, "TURN", "-", "Advanced to next turn");
    }

    /**
     * Mindig érvényes, nem igényel paramétert
     */
    @Override
    public boolean validate(Game game) {
        return true;
    }

    @Override
    public String toString() {
        return "NEXT_TURN";
    }
}

