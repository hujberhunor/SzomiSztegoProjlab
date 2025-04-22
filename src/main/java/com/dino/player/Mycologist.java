package com.dino.player;

import java.util.ArrayList;
import java.util.List;

import com.dino.core.Hypha;
import com.dino.core.Fungus;
import com.dino.tecton.Tecton;
import com.dino.util.Skeleton;

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

    public Mycologist() {
        this.mushrooms = new ArrayList<Fungus>();
    }

    /**
     * Elhelyez egy új gombatestet a paraméterként átadott tektonon,
     * ha azon van a gombász által vezérelt fajnak fonala és kellő mennyiségű spórája.
     * @param t Ezen a tektonon lesz elhelyezve a gomatest.
     */
    public void placeFungus(Tecton t) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Mycologist", "placeFungus");

        // Ellenőrizzük, hogy a tekton tartalmaz-e a gombász által vezérelt fajnak megfelelő fonalat
        boolean tectonHasHypha = false;
        for (Hypha h : t.getHyphas()) {
            if (h.getMycologist().equals(this)) {
                tectonHasHypha = true;
            }
        }
        if (!tectonHasHypha) {
            skeleton.log(
                "Nem lehet elhelyezni a gombát: nincs megfelelő gombafonál a tektonon."
            );
            skeleton.endMethod();
            return;
        }

        // Ellenőrizzük, hogy van-e elegendő spóra
        if (!t.hasSpores(this)) {
            skeleton.log(
                "Nem lehet elhelyezni a gombát: nincs elegendő spóra."
            );
            skeleton.endMethod();
            return;
        }

        // Gombatest létrehozása és hozzáadása a listához
        Fungus newFungus = new Fungus(this, t);
        mushrooms.add(newFungus);
        t.setFungus(newFungus);

        skeleton.log("Gombatest sikeresen elhelyezve a tektonon.");
        skeleton.endMethod();
    }

    public List<Fungus> getMushrooms(){
        return mushrooms;
    }

    /*
     * CSAK DEBUG ÉS TESZT JELLEGGEL 
     * INITELÉSHEZ LETT LÉRTEROZVA
     */
    void debugPlaceFungus(Tecton t){
        Fungus f = new Fungus();
        t.setFungus(f);
    }
}
