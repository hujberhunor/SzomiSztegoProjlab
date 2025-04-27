package com.dino.effects;

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

        List<Spore> prevEffects = insect.getEffects();

        // ellenőrzés, hogy már az adott spóra hatása alatt van-e
        boolean alreadyHasEffect = false;
        for (Spore effect : prevEffects) {
            if (effect.sporeType() == 1)
                alreadyHasEffect = true;
        }

        if (alreadyHasEffect) {
            logger.logError("SPORE", "ACCELERATING_EFFECT", "A rovar már gyorsító hatás alatt van!");
        } else {
            int prevActions = insect.getEntomologist().getRemainingActions();

            // Hatás alkalmazása
            insect.addEffects(this); // effekt listára
            insect.getEntomologist().increaseActions(); // +1 action

            if (insect.getEffects().contains(this)) {
                logger.logChange("INSECT", insect, "EFFECT", prevEffects, insect.getEffects());
            } else {
                logger.logError("EFFECT", "ACCELERATING EFFECT", "Nem sikerült alkalmazni a rovarra!");
            }

            if (prevActions < insect.getEntomologist().getRemainingActions()) {
                logger.logChange("ENTOMOLOGIST", insect.getEntomologist(), "REMAINING ACTIONS", prevActions,
                        insect.getEntomologist().getRemainingActions());
            } else {
                logger.logError("ENTOMOLOGIST", "", "Nem sikerült növelni az akciók számát!");
            }
        }
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("sporeType", sporeType());

        return obj;
    }

}
