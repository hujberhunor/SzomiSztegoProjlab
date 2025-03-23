package com.dino;

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

    //A gomba hatását megvalósító függvény. Ennek a spórának az esetében nem valósít meg érdemi funkciót.
    public void applyTo(Insect i) {
        i.addEffects(this);
    }
}

