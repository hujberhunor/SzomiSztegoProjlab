package com.dino.view;

import com.dino.engine.Game;
import com.dino.player.Player;

import java.util.HashMap;
import java.util.Map;

public class Scoreboard implements ModelObserver {
    private Map<Player, Integer> scores;
    private boolean isVisible;

    public Scoreboard() {
        scores = new HashMap<>();
        isVisible = true;
    }

    /**
     * A pontszámok frissítése
     * @param scores Az új pontszámok
     */
    public void updateScores(Map<Player, Integer> scores) {

    }

    /**
     * A láthatóság átváltása
     */
    public void toggleVisibility() {
        isVisible = !isVisible;
    }

    /**
     * Frissíti a komponenst a játék aktuális állapota alapján
     * @param game A játék aktuális állapota
     */
    @Override
    public void update(Game game) {

    }
}