package com.dino;

import java.util.*;

/**
 * Egy rovart reprezentáló osztály.
 */
public class Insect {
    /**
     * Megadja, hogy a rovar melyik rovarászhoz tartozik.
     */
    private Entomologist entomologist;
    /**
     * Az a tekton objektum, amin a rovar tartózkodik.
     */
    private Tecton tecton;
    /**
     * Azoknak az effekteknek a listája, amik alatt a rovar van.
     * Egy ilyen hatást egy spóra definiál.
     */
    private List<Spore> effects;

    /**
     * A rovar átlép a paraméterként kapott tektonra, amennyiben az szomszédos, és vezet át gombafonál a jelenlegi tartózkodási hely és a célként választott tekton között.
     * Visszaadja, hogy sikeres volt-e a művelet.
     * @param t
     * @return
     */
    public boolean move(Tecton t) { return true; }

    /**
     * A rovar megsemmisíti a választott tekton és a jelenlegi tartózkodási helye között futó h fonalat, amennyiben az szomszédos az övével.
     * Visszaadja, hogy sikeres volt-e a művelet.
     * @param h
     * @return
     */
    public boolean cutHypha(Hypha h) { return true; }

    /**
     * A rovar táplálkozik a saját tektonján lévő spórákból, és a tápanyagtartalmat hozzáadja a paraméterként kapott rovarász pontszámához.
     * Ha több fajta található, abból eszik, amelyikből több van (ha egyenlő, akkor véletlenszerű).
     * Ekkor az elfogyasztott spóra tápanyagtartalmától függően nő a játékos pontszáma.
     * @param e
     */
    public void consumeSpores(Entomologist e) { }

    /**
     * A rovar effektlistájához hozzáadja az új effektet.
     * @param s
     */
    public void addEffects(Spore s) { }

    /**
     * A rovar effektlistájáról eltávolítja a lejárt effektet.
     */
    public void removeExpiredEffects() { }
}

