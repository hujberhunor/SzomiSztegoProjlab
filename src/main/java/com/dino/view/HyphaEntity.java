package com.dino.view;

import com.dino.core.Hypha;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class HyphaEntity extends Entity {
    protected Tecton start;
    protected Tecton end;
    protected Mycologist mycologist;
    protected int lifespan;

    private Point2D startPos;
    private Point2D endPos;

    public void setStartPos(Point2D p) {
        this.startPos = p;
    }

    public void setEndPos(Point2D p) {
        this.endPos = p;
    }

    public HyphaEntity(Hypha h) {
        start = h.getTectons().get(0);
        end = h.getTectons().get(1);
        mycologist = h.getMycologist();
        lifespan = h.getLifespan();
    }

    /**
     * A fonal kirajzolása
     * 
     * @return A kirajzolt fonalat tartalmazó Node objektum
     */

    @Override
    public Node draw() {
        if (startPos == null || endPos == null)
            return null;

        javafx.scene.shape.Line line = new javafx.scene.shape.Line(
                startPos.getX(), startPos.getY(),
                endPos.getX(), endPos.getY());
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(3.0);
        line.setOpacity(0.7);

        return line;
    }

}