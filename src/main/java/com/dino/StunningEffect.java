package com.dino;

//Olyan spórát megvalósító osztály, aminek hatására az őt elfogyasztó rovar képtelen lesz fonalat vágni.
public class StunningEffect extends Spore {

    //Default konstruktor, beállítja a tápanyagtartalom értékét.
    public StunningEffect() {
        System.out.println("StunningEffect()");
        this.nutrientValue = 3;
    }

    //A gomba hatását megvalósító függvény. A paraméterként átadott rovar fonalvágási kísérletei sikertelenek lesznek a következő két körben.
    public void applyTo(Insect i) {
        System.out.println("StunningEffect.applyTo(Insect i)");
    }
}

