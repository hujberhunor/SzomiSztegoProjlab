package com.dino.view;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.engine.Game;
import com.dino.util.Logger;

public class GameController {
    private Game game;
    private GameBoard gameBoard;
    private CommandParser commandParser;
    private Logger logger;

    public GameController(Game game, GameBoard gameBoard, CommandParser commandParser) {
        this.game = game;
        this.gameBoard = gameBoard;
        this.commandParser = commandParser;
        this.logger = Logger.getInstance();
    }

    /**
     * Kattintások kezelése a játéktéren
     */
    public void handleClick(int x, int y) {
        // Később implementálható
    }

    /**
     * Parancsok kezelése
     */
    public void handleCommand(String commandStr) {
        try {
            Command command = commandParser.parse(commandStr);
            if (command.validate(game)) {
                command.execute(game, logger);
                gameBoard.update(game);
            } else {
                logger.logError("COMMAND", commandStr, "Érvénytelen parancs");
            }
        } catch (Exception e) {
            logger.logError("COMMAND", commandStr, "Hiba: " + e.getMessage());
        }
    }

    /**
     * Körváltás kezelése
     */
    public void handleTurn() {
        game.nextTurn();
        gameBoard.update(game);
    }
}