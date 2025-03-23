package com.dino;

//Olyan spórát megvalósító osztály, ami felgyorsítja az őt elfogyaszó rovarat, aki a következő körökben legfeljebb akár háromszor tud majd mozogni.
public class AcceleratingEffect extends Spore {
    private static final int ACCELERATING_EFFECT_NUTRIENT_VALUE = 1;

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public AcceleratingEffect(Mycologist mycologist) {
        super(mycologist, ACCELERATING_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return ACCELERATING_EFFECT_NUTRIENT_VALUE;
    }

    public String toString(){
        return "Accelerating Spore";
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar a következő két kör alatt eggyel több akciót használhat ki mozgásra.
    public void applyTo(Insect i) {
        i.addEffects(this);
    }
}