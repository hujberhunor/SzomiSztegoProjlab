package com.dino.view;

import com.dino.engine.Game;

public class GameWindow {
    private GameBoard gameBoard;
    private TopBar topBar;
    private BottomBar bottomBar;
    private Scoreboard scoreboard;

    /**
     * Inicializálja a GUI-t a játék példány alapján
     * @param game A játék példány
     */
    public void startGame(Game game) {
        gameBoard = new GameBoard();
        bottomBar = new BottomBar();
        scoreboard = new Scoreboard();
        topBar = new TopBar(scoreboard);
        gameBoard.update(game);
        topBar.update(game);
        bottomBar.update(game);
        scoreboard.update(game);
    }

    /**
     * Frissíti az összes komponenst a játék állapota alapján
     */
    public void refreshUI() {

    }
}