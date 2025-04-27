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

//Olyan spórát megvalósító osztály, aminek nincs hatása az őt elfogyasztó rovarra.
public class SporeNoEffect extends Spore {
    private static final int NO_EFFECT_NUTRIENT_VALUE = 1;

    // Default konstruktor, beállítja a tápanyagtartalom értékét.
    public SporeNoEffect(Mycologist mycologist) {
        super(mycologist, NO_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return NO_EFFECT_NUTRIENT_VALUE;
    }

    public String toString() {
        return "Regular Spore";
    }

    @Override
    public int sporeType() {
        return 0;
    }

    // A gomba hatását megvalósító függvény. Ennek a spórának az esetében nem
    // valósít meg érdemi funkciót.
    public void applyTo(Insect insect) {
        EntityRegistry registry = EntityRegistry.getInstance();
        Logger logger = Logger.getInstance();

        List<Spore> prevEffects = new ArrayList<>(insect.getEffects());

        insect.addEffects(this);

        logger.logChange("INSECT", insect, "EFFECT", prevEffects, insect.getEffects());
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("sporeType", sporeType());

        return obj;
    }

}
