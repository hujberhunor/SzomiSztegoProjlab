package com.dino.view;

import com.dino.core.Spore;
import com.dino.player.Mycologist;
import javafx.scene.Node;

public class SporeEntity extends Entity {
    protected Mycologist mycologist;
    protected int effectDuration;
    protected int nutrientValue;
    protected int sporeType;

    public SporeEntity(Spore s) {
        mycologist = s.getSpecies();
        effectDuration = s.getEffectDuration();
        nutrientValue = s.getNutrientValue();
        sporeType = s.sporeType();
    }

    /**
     * A spóra kirajzolása
     * 
     * @return A kirajzolt spórát tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null; // A spóra megjelenítését a Tecton végzi, nincs szükség önálló vizualizációra
    }
}
