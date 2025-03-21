package com.dino;

//Absztrakt osztály, aminek leszármazottai a specifikus spóratípusokat valósítják meg.
//Az osztály attribútumaiban számontartja, hogy melyik gombászhoz tartozik a spóra, hány körig tart a hatása,
//illetve hogy elfogyasztásakor a rovarász mennyi pontot kap (ez a tápanyagtartalom).
//A spóra altípusok ősosztálya
public abstract class Spore {
    //Attribútumok. Sorrendben: a gombász, akitől ered a spóra, hatásának hossza illetve a tápanyagtartalom
    protected Mycologist species;
    protected int effectDuration;
    protected int nutrientValue;

    //Default konstruktor
    public Spore() {
        //TODO
    }

    //--------------------------------------------------------------
    //OBSOLETE FÜGGVÉNYEK AZ INTERFACE-BŐL.
    //--------------------------------------------------------------
    /*
    public void move() {
        System.out.println("Spore.move()");
    }
    //public boolean eat(Entomologist e)?
    public void eat() {
        System.out.println("Spore.eat()");
    }
    //public boolean cut(Tecton t, Hypha h)?
    public void cut() {
        System.out.println("Spore.cut()");
    }
    public void update() { }
    */
    //--------------------------------------------------------------
    //OBSOLETE FÜGGVÉNYEK AZ INTERFACE-BŐL.
    //--------------------------------------------------------------

    //Absztrakt függvény, amit a leszármazott spóratípusok megvalósítanak. Ezzel a függvénnyel fejtik ki hatásukat a paraméterként átadott rovaron.
    public abstract void applyTo(Insect i);
}

