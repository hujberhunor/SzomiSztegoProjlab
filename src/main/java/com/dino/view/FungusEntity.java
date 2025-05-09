package com.dino.view;

import com.dino.player.Mycologist;
import javafx.scene.Node;

public class FungusEntity extends Entity {
    protected Mycologist mycologist;
    protected int charge;
    protected int lifespan;

    /**
     * A gomba kirajzolása
     * @return A kirajzolt gombát tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null;
    }
}