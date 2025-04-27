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

//Olyan spórát megvalósító osztály, ami lelassítja az őt elfogyaszó rovarat, aki a következő körökben legfeljebb egyszer tud majd mozogni.
public class SlowingEffect extends Spore {
    private static final int SLOWING_EFFECT_NUTRIENT_VALUE = 2;

    // Default konstruktor, beállítja a tápanyagtartalom értékét.
    public SlowingEffect(Mycologist mycologist) {
        super(mycologist, SLOWING_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return SLOWING_EFFECT_NUTRIENT_VALUE;
    }

    public String toString() {
        return "Slowing Spore";
    }

    @Override
    public int sporeType() {
        return 4;
    }

    // A gomba hatását megvalósító függvény. A paraméterként átadott rovar a
    // következő két körben legfeljebb egy akciót használhat ki mozgásra.
    public void applyTo(Insect insect) {
        EntityRegistry registry = EntityRegistry.getInstance();
        Logger logger = Logger.getInstance();

        List<Spore> prevEffects = new ArrayList<>(insect.getEffects());

        // ellenőrzés, hogy már az adott spóra hatása alatt van-e
        boolean alreadyHasEffect = false;
        for (Spore effect : prevEffects) {
            if (effect.sporeType() == 4)
                alreadyHasEffect = true;
        }

        if (alreadyHasEffect) {
            logger.logError("SPORE", "SLOWING_EFFECT", "A rovar már lassító hatás alatt van!");
        } else {
            int prevActions = insect.getEntomologist().getRemainingActions();

            insect.addEffects(this);
            insect.getEntomologist().setActions(1);

            logger.logChange("INSECT", insect, "EFFECT", prevEffects, insect.getEffects());
            logger.logChange("ENTOMOLOGIST", insect.getEntomologist(), "REMAINING ACTIONS", prevActions, 1);
        }
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("sporeType", sporeType());

        return obj;
    }

}
