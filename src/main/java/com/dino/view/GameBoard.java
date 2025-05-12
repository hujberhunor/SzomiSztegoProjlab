package com.dino.view;

import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;

import java.util.HashMap;
import java.util.Map;

public class GameBoard implements ModelObserver {
    private Map map;
    private GameController controller;
    private Map<Tecton, Polygon> tectonShapes;
    private Map<Object, Node> entityNodes;

    public GameBoard() {
        tectonShapes = new HashMap<>();
        entityNodes = new HashMap<>();
    }

    /**
     * A teljes játéktér kirajzolása
     */
    public void render() {

    }

    /**
     * A játéktér frissítése a játék állapota alapján
     * @param game A játék aktuális állapota
     */
    @Override
    public void update(Game game) {

    }

    /**
     * Egy adott tecton kiemelése
     * @param t A kiemelendő tecton
     */
    public void highlightTecton(Tecton t) {

    }
}