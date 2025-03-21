package com.dino;

import java.util.ArrayList;
import java.util.List;

/**
 * A Player osztály leszármazottja, egy gombászt reprezentál. Megvalósítja a gombászokra specifikus olyan akciót,
 * ami nem egy specifikus gombához tartozik, hanem magához a játékoshoz, illetve számontartja a játékos gombatestjeit.
 */
public class Mycologist extends Player {

    private List<Fungus> fungi = new ArrayList<>();
    // public boolean placeFungus(Tecton t);
} // End of Mychoogist
