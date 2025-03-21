package com.dino;

//Olyan spórát megvalósító osztály, aminek hatására őt elfogyaszó rovar a következő körökre teljesen mozgásképtelenné válik.
//Ez a hatás felülír minden más mozgást befolyásoló effektet.
public class ParalyzingEffect extends Spore {

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public ParalyzingEffect() {
        System.out.println("ParalyzingEffect()");
        this.nutrientValue = 4;
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar a következő két körben nem tud akciót kihasználni mozgásra.
    public void applyTo(Insect i) {
        System.out.println("ParalyzingEffect.applyTo(Insect i)");
    }
}

