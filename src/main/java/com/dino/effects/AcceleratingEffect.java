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

//Olyan spórát megvalósító osztály, ami felgyorsítja az őt elfogyaszó rovarat, aki a következő körökben egyel több akciót tud végrehajtani.
public class AcceleratingEffect extends Spore {

    private static final int ACCELERATING_EFFECT_NUTRIENT_VALUE = 1;

    // Default konstruktor, beállítja a tápanyagtartalom értékét.
    public AcceleratingEffect(Mycologist mycologist) {
        super(mycologist, ACCELERATING_EFFECT_NUTRIENT_VALUE);
    }

    // A spóra tápértékét visszaadó függvény.
    @Override
    public int getNutrientValue() {
        return ACCELERATING_EFFECT_NUTRIENT_VALUE;
    }

    // A spóra fajtáját stringgé alakító függvény.
    @Override
    public String toString() {
        return "Accelerating Spore";
    }

    // A spóra típusát reprezentáló integert visszaadó függvény.
    @Override
    public int sporeType() {
        return 1;
    }

    // A gomba hatását megvalósító függvény. A paraméterként átadott rovar a
    // következő két kör alatt eggyel több akciót használhat ki mozgásra.
    @Override
    public void applyTo(Insect insect) {
        EntityRegistry registry = EntityRegistry.getInstance();
        Logger logger = Logger.getInstance();

        List<Spore> prevEffects = new ArrayList<>(insect.getEffects());

        // ellenőrzés, hogy már az adott spóra hatása alatt van-e
        boolean alreadyHasEffect = false;
        for (Spore effect : prevEffects) {
            if (effect.sporeType() == 1)
                alreadyHasEffect = true;
        }

        if (alreadyHasEffect) {
            logger.logError("SPORE", this.getClass().getSimpleName(), "A rovar már gyorsító hatás alatt van!");
        } else {
            // Hatás alkalmazása
            insect.addEffects(this); // effekt listára
            insect.setExtraMove(true);

            logger.logChange("INSECT", insect, "EFFECT", prevEffects, insect.getEffects());
        }
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("sporeType", sporeType());

        return obj;
    }

}
