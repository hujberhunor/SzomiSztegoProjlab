package com.dino;

//Olyan spórát megvalósító osztály, ami felgyorsítja az őt elfogyaszó rovarat, aki a következő körökben legfeljebb akár háromszor tud majd mozogni.
public class AcceleratingEffect extends Spore {

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public AcceleratingEffect() {
        System.out.println("AcceleratingEffect()");
        this.nutrientValue = 1;
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar a következő két kör alatt eggyel több akciót használhat ki mozgásra.
    public void applyTo(Insect i) {
        System.out.println("AcceleratingEffect.applyTo(Insect i)");
    }
}