package com.dino.view;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.engine.Game;

public class GameController {
    private Game game;
    private GameBoard gameBoard;
    private CommandParser commandParser;

    public GameController(Game game, GameBoard gameBoard, CommandParser commandParser) {
        this.game = game;
        this.gameBoard = gameBoard;
        this.commandParser = commandParser;
    }

    /**
     * Kattintások kezelése a játéktéren
     *
     * @param x Az X koordináta
     * @param y Az Y koordináta
     */
    public void handleClick(int x, int y) {

    }

    /**
     * Parancsok kezelése
     *
     * @param command A feldolgozandó parancs
     */
    public void handleCommand(String command) {
        Command parsedCommand = commandParser.parse(command);
    }

    /**
     * Körváltás kezelése
     */
    public void handleTurn() {

    }
}