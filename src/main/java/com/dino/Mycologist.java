package com.dino;

import java.util.List;

/**
 * Ez az osztály egy gombászt reprezentál.
 * Megvalósítja a gombászokra specifikus olyan akciót, ami nem egy specifikus gombához tartozik,
 * hanem magához a játékoshoz, illetve számontartja a játékos gombatestjeit.
 */
public class Mycologist {
    /**
     * Egy lista, ami a gombász által vezérelt gombatesteket tárolja.
     */
    List<Fungus> mushrooms;

    /**
     * Elhelyez egy új gombatestet a paraméterként átadott tektonon,
     * ha azon van a gombász által vezérelt fajnak fonala és kellő mennyiségű spórája.
     * @param t Ezen a tektonon lesz elhelyezve a gomatest.
     */
    void placeFungus(Tecton t){

    }
}
