package com.dino.view;

import com.dino.engine.Game;

public interface ModelObserver {
    /**
     * Frissíti a komponenst a játék aktuális állapota alapján.
     * @param game A játék aktuális állapota
     */
    public void update(Game game);
}