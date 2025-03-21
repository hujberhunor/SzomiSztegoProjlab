package com.dino;

import java.util.List;
import java.util.Map;

/**
 * Ennek az absztrakt osztálynak a leszármazottjai reprezentálják a játékteret alkotó egységeket, vagy mezőket.
 * Tárolja a mezőket alkotó kisebb hatszögeket, illetve az egész egységre vonatkozó tulajdonságokat és annak állapotát.
 * A tekton altípusoknak az ősosztálya.
 */
public abstract class Tecton {
    // Attribútumok
    protected boolean fungiEnabled;
    protected int hyphaLimit;
    protected int hyphaLifespan;
    protected double breakChance;
    protected int breakCount;
    protected List<Hexagon> hexagons;
    protected Fungus fungus;
    protected Insect insect;
    protected Map<Mycologist, Integer> spores;
    protected List<Hypha> hyphas;
    
    /**
     * Alapértelmezett konstruktor
     */
    public Tecton() {
        // TODO IMPLEMENTÁLNI
    }
    
    /**
     * A tektonon elhelyez egy darab, m gombász gombájából származó spórát.
     * 
     * @param m A gombász, akinek a gombájából a spóra származik
     */
    public void addSpores(Mycologist m) {
        // TODO IMPLEMENTÁLNI
    }
    
    /**
     * A tektonról eltávolít egy darab, m gombász gombájából származó spórát, amennyiben lehetséges.
     * 
     * @param m A gombász, akinek a gombájából a spóra származik
     */
    public void removeSpores(Mycologist m) {
        // TODO IMPLEMENTÁLNI
    }
    
    /**
     * A tektonon elhelyezi a paraméterként kapott gombafonalat.
     * A Hypha osztály tárolja, hogy melyik gombász gombájától származik.
     * 
     * @param h Az elhelyezendő gombafonál
     */
    public void addHypha(Hypha h) {
        // TODO IMPLEMENTÁLNI
    }
    
    /**
     * Minden kör után hívódó függvény, ami breakChance eséllyel, hatszögek mentén létrehoz két új tektont
     * a saját helyén, míg magát megszűnteti. Az új tektonok tulajdonságai egyeznek az őket létrehozó tulajdonságaival,
     * amiről a gombatest és rovarok véletlenszerűen választott töredékekre kerülnek át, a gombafonalak pedig megsemmisülnek.
     * Visszaadja a két új tektont listaként.
     * 
     * @param breakChance Törési esély
     * @return Az újonnan létrehozott két tekton listája
     */
    public List<Tecton> split(double breakChance) {
        // TODO IMPLEMENTÁLNI
        return null;
    }
    
    /**
     * A fonalakat kezelő virtuális függvény, amelyet az alosztályok felülírnak.
     * 
     * @param h A kezelendő gombafonál
     */
    protected abstract void handleHypha(Hypha h);
}