package com.dino.effects;

import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.player.Mycologist;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

import java.util.List;

//Olyan spórát megvalósító osztály, aminek nincs hatása az őt elfogyasztó rovarra.
public class SporeNoEffect extends Spore {
    private static final int NO_EFFECT_NUTRIENT_VALUE = 1;

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public SporeNoEffect(Mycologist mycologist) {
        super(mycologist, NO_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return NO_EFFECT_NUTRIENT_VALUE;
    }

    public String toString(){
        return "Regular Spore";
    }

    @Override
    public int sporeType() {
        return 0;
    }

    //A gomba hatását megvalósító függvény. Ennek a spórának az esetében nem valósít meg érdemi funkciót.
    public void applyTo(Insect insect) {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        List<Spore> prevEffects = insect.getEffects();

        insect.addEffects(this);

        if(insect.getEffects().contains(this)){
            logger.logChange("INSECT", insect, "EFFECT", prevEffects, insect.getEffects());
        }
        else {
            logger.logError("EFFECT", "NO EFFECT", "Nem sikerült alkalmazni a rovarra!");
        }
    }
}

