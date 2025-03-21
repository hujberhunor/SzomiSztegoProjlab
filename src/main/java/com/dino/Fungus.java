package com.dino;

import java.util.ArrayList;
import java.util.List;

/**
 * A gombász játékosok által irányított gombát reprezentálja, amely gombafonalak és spórák segítségével terjeszkedik a tektonokon keresztül.
 * A gombák szaporodását a játékos dönti el
 */
public class Fungus {

    private Mycologist species;
    private Tecton tecton;
    private int charge;
    private int lifespan;
    private List<Hypha> hyphas = new ArrayList<>();
    private List<Spore> spores = new ArrayList<>();

    public Fungus(Mycologist species, Tecton tecton) {
        this.species = species;
        this.tecton = tecton;
        this.charge = 0;
        this.lifespan = 5;
        this.hyphas = new ArrayList<>();
        this.spores = new ArrayList<>();
    }

    public void spreadSpores(List<Tecton> t);

    public boolean growHypha(List<Tecton> t);
}
