package com.dino.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A játékteret alkotó legkisebb egységeket reprezentáló osztály.
 * A játék kezdetekor először ezek az objektumok jönnek létre,
 * amik majd véletlenszerűen tektonokká rendeződnek.
 */
public class Hexagon {
    // Attribútumok
    private int id;
    private List<Hexagon> neighbours;

    /**
     * Hexagon konstruktor
     * 
     * @param id Egyedi azonosító
     */
    public Hexagon(int id) {
        this.id = id;
        this.neighbours = new ArrayList<>();
    }

    /**
     * Id getter
     * 
     * @return A hexagon egyedi azonosítója
     */
    public int getId() {
        return id;
    }

    /**
     * Szomszédok beállítása
     * 
     * @param neighbours Szomszédos hatszögek listája
     */
    public void setNeighbours(List<Hexagon> neighbours) {
        this.neighbours = neighbours;
    }

    /**
     * Szomszédok lekérése
     * 
     * @return Szomszédos hatszögek listája
     */
    public List<Hexagon> getNeighbours() {
        return neighbours;
    }

    /**
     * Paraméter nélkül hívható függvény, ami megszűnteti a hatszöget,
     * és törli a játéktérről. Annak első generálásakor lesz szerepe.
     */
    public void destroy() {
        // Minden szomszéd listájából eltávolítjuk a törlendőt
        if (neighbours != null) {
            for (Hexagon neighbour : neighbours) {
                if (neighbour.getNeighbours() != null) {
                    neighbour.getNeighbours().remove(this);
                }
            }
            // Töröljük a saját szomszédaink listáját
            neighbours.clear();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}