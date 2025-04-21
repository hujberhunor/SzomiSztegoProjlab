package com.dino.effects;

import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.player.Mycologist;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

import java.util.List;

//Olyan spórát megvalósító osztály, aminek hatására őt elfogyaszó rovar a következő körökre teljesen mozgásképtelenné válik.
//Ez a hatás felülír minden más mozgást befolyásoló effektet.
public class ParalyzingEffect extends Spore {

    private static final int PARALYZING_EFFECT_NUTRIENT_VALUE = 4;

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public ParalyzingEffect(Mycologist mycologist) {
        super(mycologist, PARALYZING_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return PARALYZING_EFFECT_NUTRIENT_VALUE;
    }

    public String toString(){
        return "Paralyzing Spore";
    }

    @Override
    public int sporeType() {
        return 3;
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar a következő két körben nem tud akciót kihasználni mozgásra.
    @Override
    public void applyTo(Insect insect) {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        List<Spore> prevEffects = insect.getEffects();
        int prevActions = insect.getEntomologist().getRemainingActions();

        // Hatás alkalmazása
        insect.addEffects(this); // effekt listára
        insect.getEntomologist().setActions(0);

        if(insect.getEffects().contains(this)){
            logger.logChange("INSECT", insect, "EFFECT", prevEffects, insect.getEffects());
        }
        else {
            logger.logError("EFFECT", "PARALYZING EFFECT", "Nem sikerült alkalmazni a rovarra!");
        }

        logger.logChange("ENTOMOLOGIST", insect.getEntomologist(), "ACTIONS", prevActions, 0);
    }
}
