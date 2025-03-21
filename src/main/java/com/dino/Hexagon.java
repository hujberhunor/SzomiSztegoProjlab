package com.dino;

import java.util.ArrayList;
import java.util.List;

/*
 * A játékteret alkotó legkisebb egységeket reprezentáló osztály. A játék kezdetekor először ezek az objektumok jönnek létre,
 * amik majd véletlenszerűen tektonokká rendeződnek.
 */
public class Hexagon {

    public int id;
    public List<Hexagon> neighbours = new ArrayList<>();

    // Konstruktor
    public Hexagon(int id) {
        this.id = id;
        this.neighbours = new ArrayList<>();
    }

    // Szomszéd hozzáadása
    public void addNeighbour(Hexagon neighbour) {
        this.neighbours.add(neighbour);
    }
} // end of Hexagon
