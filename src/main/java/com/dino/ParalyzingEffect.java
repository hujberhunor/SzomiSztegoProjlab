package com.dino;

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

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar a következő két körben nem tud akciót kihasználni mozgásra.
    public void applyTo(Insect i) {
        i.addEffects(this);
    }
}

