package com.dino.effects;

import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.player.Mycologist;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

import java.util.List;

//Olyan spórát megvalósító osztály, aminek hatására az őt elfogyasztó rovar képtelen lesz fonalat vágni.
public class StunningEffect extends Spore {
    private static final int STUNNING_EFFECT_NUTRIENT_VALUE = 3;

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public StunningEffect(Mycologist mycologist) {
        super(mycologist, STUNNING_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return STUNNING_EFFECT_NUTRIENT_VALUE;
    }

    public String toString(){
        return "Stunning Spore";
    }

    @Override
    public int sporeType() {
        return 5;
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar fonalvágási kísérletei sikertelenek lesznek a következő két körben.
    public void applyTo(Insect insect) {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        List<Spore> prevEffects = insect.getEffects();

        insect.addEffects(this);

        if(insect.getEffects().contains(this)){
            logger.logChange("INSECT", insect, "EFFECT", prevEffects, insect.getEffects());
        }
        else {
            logger.logError("EFFECT", "STUNNING EFFECT", "Nem sikerült alkalmazni a rovarra!");
        }
    }
}

