package com.dino.view;

import com.dino.core.Fungus;
import com.dino.player.Mycologist;
import javafx.scene.Node;

public class FungusEntity extends Entity {
    protected Mycologist mycologist;
    protected int charge;
    protected int lifespan;

    public FungusEntity(){
        mycologist = null;
        charge = 0;
        lifespan = 0;
    }

    public FungusEntity(Fungus f){
        mycologist = f.getSpecies();
        charge = f.getCharge();
        lifespan = f.getLifespan();
    }

    /**
     * A gomba kirajzolása
     * @return A kirajzolt gombát tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null;
    }
}