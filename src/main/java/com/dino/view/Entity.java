package com.dino.view;

import javafx.geometry.Point2D;
import javafx.scene.Node;

public abstract class Entity {
    protected String id;
    protected Point2D location;
    protected boolean isSelected;
    protected boolean isVisible;

    public Entity() {
        id = null;
        location = null;
        isSelected = false;
        isVisible = false;
    }

    public Entity(String id, Point2D location) {
        this.id = id;
        this.location = location;
        this.isSelected = false;
        this.isVisible = true;
    }

    /**
     * Az entitás vizuális megjelenítése
     * @return A vizuális reprezentációt tartalmazó Node objektum
     */
    public Node getVisualRepresentation() {
        return null;
    }

    /**
     * Az entitás frissítése a modell alapján
     * @param modelEntity A modell entitás objektum
     */
    public void update(Object modelEntity) {

    }

    /**
     * Absztrakt metódus az entitás kirajzolására
     * @return A kirajzolt entitást tartalmazó Node objektum
     */
    public abstract Node draw();
}