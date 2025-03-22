package com.dino;

//Olyan spórát megvalósító osztály, ami lelassítja az őt elfogyaszó rovarat, aki a következő körökben legfeljebb egyszer tud majd mozogni.
public class SlowingEffect extends Spore {
    private static final int SLOWING_EFFECT_NUTRIENT_VALUE = 2;

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public SlowingEffect(Mycologist mycologist) {
        super(mycologist, SLOWING_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return SLOWING_EFFECT_NUTRIENT_VALUE;
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar a következő két körben legfeljebb egy akciót használhat ki mozgásra.
    public void applyTo(Insect i) {
        i.addEffects(this);
    }
}

