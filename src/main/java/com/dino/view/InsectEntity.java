package com.dino.view;

import com.dino.player.Entomologist;
import javafx.scene.Node;
import javafx.scene.effect.Effect;

import java.util.List;

public class InsectEntity extends Entity {
    protected int movementPoints;
    protected List<Effect> effects;
    protected Entomologist entomologist;

    /**
     * A rovar kirajzolása
     * @return A kirajzolt rovart tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null;
    }
}