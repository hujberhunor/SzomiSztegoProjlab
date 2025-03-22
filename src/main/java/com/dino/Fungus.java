package com.dino;

import java.util.*;

/**
 * Egy gombatestet reprezentáló osztály.
 */
class Fungus {

    /**
     * Az a gombász, akihez a gombatest tartozik.
     * Tulajdonképpen a gomba faját jelenti.
     */
    private Mycologist species;
    /**
     * Azt a tektont tárolja el, amin a gombatest található.
     */
    private Tecton tecton;
    /**
     * Egy egész szám nulla és három között, ami megadja, hogy a gombatest mennyire van feltöltve, és kész-e a spóraszórásra.
     * Értéke körönként automatikusan eggyel nő.
     * Ha kettő, a gombatest a szomszédos tektonokra tud spórákat szórni, ha három, akkor azok szomszédjaira is.
     */
    private int charge;
    /**
     * A gombatest élettartama
     */
    private int lifespan;
    /**
     * A gombatestből eredő gombafonalakat tartalmazó lista.
     * Minden eleme egy új tekton irányába indított fonál, vagy egy elágazás.
     */
    private List<Hypha> hyphas;
    /**
     * A gombatest által kibocsátott spórák listája.
     */
    private List<Spore> spores;

    public Fungus() {
        this.species = new Mycologist(); // Helyes példányosítás
        this.tecton = new NoFungiTecton(); // Példa: Egy megfelelő Tecton osztály
        this.charge = 0;
        this.lifespan = 0;
        this.hyphas = new ArrayList<>();
        this.spores = new ArrayList<>();
    }

    public Fungus(Mycologist m, Tecton t) {
        this.species = m;
        this.tecton = t;
        this.charge = 0;
        this.lifespan = 0;
        this.hyphas = new ArrayList<>();
        this.spores = new ArrayList<>();
    }

    /**
     * A gombatest spórákat szór a Charge attribútuma értéke szerint
     * vagy a szomszédos tektonokra, vagy azok szomszédjaira is.
     * @param t
     */
    public void spreadSpores(List<Tecton> t) {}

    /**
     * A függvény hívásakor a gombatest a paraméterként kapott,
     * legfeljebb kételemű listában tárolt tektonokra növeszt gombafonalakat.
     * @param t
     * @return
     */
    public boolean growHypha(List<Tecton> t) {
        return false;
    }
}
