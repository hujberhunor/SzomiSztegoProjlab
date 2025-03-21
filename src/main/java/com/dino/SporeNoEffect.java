package com.dino;

//Olyan spórát megvalósító osztály, aminek nincs hatása az őt elfogyasztó rovarra.
public class SporeNoEffect extends Spore {

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public SporeNoEffect() {
        System.out.println("SporeNoEffect()");
        this.nutrientValue = 1;
    }

    //A gomba hatását megvalósító függvény. Ennek a spórának az esetében nem valósít meg érdemi funkciót.
    public void applyTo(Insect i) {
        System.out.println("SporeNoEffect.applyTo(Insect i)");
    }
}

