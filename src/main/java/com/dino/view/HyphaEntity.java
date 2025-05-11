package com.dino.view;

import com.dino.core.Hypha;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import javafx.scene.Node;

import java.awt.geom.Point2D;

public class HyphaEntity extends Entity {
    protected Tecton start;
    protected Tecton end;
    protected Mycologist mycologist;
    protected int lifespan;

    public HyphaEntity(Hypha h) {
        start = h.getTectons().get(0);
        end = h.getTectons().get(1);
        mycologist = h.getMycologist();
        lifespan = h.getLifespan();
    }

    /**
     * A fonal kirajzolása
     * @return A kirajzolt fonalat tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null;
    }
}