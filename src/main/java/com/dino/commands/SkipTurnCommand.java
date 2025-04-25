package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class SkipTurnCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        logger.logChange("GAME", game, "TURN", "-", "Skipped to next turn");
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
        return "SKIP_TURN";
    }
}

