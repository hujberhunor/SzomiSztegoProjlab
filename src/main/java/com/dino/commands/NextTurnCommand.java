package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class NextTurnCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        // Jelenlegi játékos akcióinak nullázása
        game.getCurrentPlayer().remainingActions = 0;
        
        // Következő játékosra lépés
        int result = game.nextTurn();
        
        // Játék állapot értékelése
        if (result == 0) {
            // Ha egy kör véget ért, és az utolsó kör volt
            if (game.getCurrentRound() >= game.getTotalRounds()) {
                game.endGame();
                logger.logChange("GAME", game, "STATE", "RUNNING", "ENDED");
                logger.logChange("GAME", game, "TURN", "-", "Game ended");
            } else {
                // Ha nem az utolsó kör volt, akkor új kör indítása
                game.nextRound();
                logger.logChange("GAME", game, "TURN", "-", "New round started");
            }
        } else {
            logger.logChange("GAME", game, "TURN", "-", "Turn ended");
        }
        
        // Értesítjük az observer-eket
        game.notifyObservers();
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