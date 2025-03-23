package com.dino;

//Olyan spórát megvalósító osztály, aminek hatására az őt elfogyasztó rovar képtelen lesz fonalat vágni.
public class StunningEffect extends Spore {
    private static final int STUNNING_EFFECT_NUTRIENT_VALUE = 3;

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public StunningEffect(Mycologist mycologist) {
        super(mycologist, STUNNING_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return STUNNING_EFFECT_NUTRIENT_VALUE;
    }

    public String toString(){
        return "Stunning Spore";
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar fonalvágási kísérletei sikertelenek lesznek a következő két körben.
    public void applyTo(Insect i) {
        i.addEffects(this);
    }
}

