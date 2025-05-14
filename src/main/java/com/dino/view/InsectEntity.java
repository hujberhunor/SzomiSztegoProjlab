package com.dino.view;

import com.dino.core.*;
import com.dino.player.Entomologist;
import javafx.scene.Node;

import java.util.List;

public class InsectEntity extends Entity {
    protected Insect insect;
    /*
    protected int movementPoints;
    protected List<Spore> effects;
    protected Entomologist entomologist;
    */
    public InsectEntity(Insect i) {
        insect = i;
        // effects = i.getEffects();
        // entomologist = i.getEntomologist();
    }

    /**
     * A rovar kirajzolása
     * @return A kirajzolt rovart tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null;
    }
}