package com.dino.view;

import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import javafx.scene.Node;

public class HyphaEntity extends Entity {
    protected Tecton start;
    protected Tecton end;
    protected Mycologist mycologist;
    protected int lifespan;

    /**
     * A fonal kirajzolása
     * @return A kirajzolt fonalat tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null;
    }
}