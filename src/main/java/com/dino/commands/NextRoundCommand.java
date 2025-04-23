package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class NextRoundCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        game.nextRound();
        logger.logChange("GAME", game, "ROUND", "-", "Advanced to next round");
    }

    /**
     * Mindig érvényes, mert nincs paraméter
     */
    @Override
    public boolean validate(Game game) {
        return true;
    }

    @Override
    public String toString() {
        return "NEXT_ROUND";
    }
}
