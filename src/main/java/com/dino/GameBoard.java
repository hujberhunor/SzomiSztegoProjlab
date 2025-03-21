package com.dino;

import java.util.*;

/**
 * A játékteret kezelő és megvalósító osztály.
 * Eltárolja az azt alkotó összes tektont, amiken keresztül el tudja érni azok szomszédjait.
 */
public class GameBoard {
    /**
     * A játéktér összes tektonját tároló lista.
     */
    private List<Tecton> tectons;

    /**
     * Paraméter nélkül hívható függvény, ami legenerálja a játékteret.
     * Létrehozza a hatszögeket, kitöröl párat véletlenszerűen, a maradékot pedig tektonokká köti össze,
     * a végeredményt pedig felveszi a tectons listába.
     */
    public void generateBoard() { }

    /**
     * Listaként visszaadja a paraméterként kapott tektonnal szomszédos összes másik tektont.
     * @param t Vizsgálandó tekton.
     * @return A vizsgált tekton összes szomszédja.
     */
    public List<Tecton> getNeighbors(Tecton t) { return new ArrayList<>(); }

    /**
     * A körök végén a tektonok törését kezelő függvény.
     */
    public void breakHandler() { }
}

