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

    @Override
    public void applyTo(Insect insect) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("AcceleratingEffect", "applyTo");

        // Hatás alkalmazása
        insect.addEffects(this); // effekt listára
        insect.getEntomologist().increaseActions(); // +1 action

        skeleton.log(
            "AcceleratingEffect hatás alkalmazva: +1 akció a rovarásznak."
        );
        skeleton.endMethod();
    }
}
