package com.dino.view;

import com.dino.engine.Game;
import com.dino.player.Player;

public class TopBar implements ModelObserver {
    private int currentTurn;
    private Player currentPlayer;

    /**
     * Az aktuális kör frissítése
     * @param turn Az új kör száma
     */
    public void updateTurn(int turn) {

    }

    /**
     * Az aktuális játékos frissítése
     * @param player Az új játékos
     */
    public void updatePlayer(Player player) {

    }

    /**
     * Frissíti a komponenst a játék aktuális állapota alapján
     * @param game A játék aktuális állapota
     */
    @Override
    public void update(Game game) {

    }
}