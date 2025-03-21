package com.dino;

//Olyan spórát megvalósító osztály, ami lelassítja az őt elfogyaszó rovarat, aki a következő körökben legfeljebb egyszer tud majd mozogni.
public class SlowingEffect extends Spore {

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public SlowingEffect() {
        System.out.println("SlowingEffect()");
        this.nutrientValue = 2;
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar a következő két körben legfeljebb egy akciót használhat ki mozgásra.
    public void applyTo(Insect i) {
        System.out.println("SlowingEffect.applyTo(Insect i)");
    }
}

