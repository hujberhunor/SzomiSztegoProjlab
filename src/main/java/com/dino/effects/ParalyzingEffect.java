package com.dino.effects;

import java.util.ArrayList;
import java.util.List;

import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.player.Mycologist;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.google.gson.JsonObject;

//Olyan spórát megvalósító osztály, aminek hatására őt elfogyaszó rovar a következő körökre teljesen mozgásképtelenné válik.
//Ez a hatás felülír minden más mozgást befolyásoló effektet.
public class ParalyzingEffect extends Spore {

    private static final int PARALYZING_EFFECT_NUTRIENT_VALUE = 4;

    // Default konstruktor, beállítja a tápanyagtartalom értékét.
    public ParalyzingEffect(Mycologist mycologist) {
        super(mycologist, PARALYZING_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return PARALYZING_EFFECT_NUTRIENT_VALUE;
    }

    public String toString() {
        return "Paralyzing Spore";
    }

    @Override
    public int sporeType() {
        return 3;
    }

    // A gomba hatását megvalósító függvény. A paraméterként átadott rovar a
    // következő két körben nem tud akciót kihasználni mozgásra.
    @Override
    public void applyTo(Insect insect) {
        EntityRegistry registry = EntityRegistry.getInstance();
        Logger logger = Logger.getInstance();

        List<Spore> prevEffects = new ArrayList<>(insect.getEffects());

        // ellenőrzés, hogy már az adott spóra hatása alatt van-e
        boolean alreadyHasEffect = false;
        for (Spore effect : prevEffects) {
            if (effect.sporeType() == 3)
                alreadyHasEffect = true;
        }

        if (alreadyHasEffect) {
            logger.logError("SPORE", "PARALYZING_EFFECT", "A rovar már bénító hatás alatt van!");
        } else {
            int prevActions = insect.getEntomologist().getRemainingActions();

            // Hatás alkalmazása
            insect.addEffects(this); // effekt listára
            insect.getEntomologist().setActions(0);

            logger.logChange("INSECT", insect, "EFFECT", prevEffects, insect.getEffects());
            logger.logChange("ENTOMOLOGIST", insect.getEntomologist(), "ACTIONS", prevActions, 0);
        }
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("sporeType", sporeType());

        return obj;
    }

}
