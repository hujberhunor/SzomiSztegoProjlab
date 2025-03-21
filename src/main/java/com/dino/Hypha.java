package com.dino;

import java.util.ArrayList;
import java.util.List;

/**
 * A gombafonalakat reprezentáló osztály. Egy objektum a növesztés sorrendjében tartalmazza a tektonokat,
 * amiken keresztül nő, hogy a fonál elszakadása esetén (rovar vagy törés hatására) a szekvenciából egyértelmű
 * legyen, hogy a fonál melyik fele nem kapcsolódik már a gombatestből, amiből származnak.
 */
public class Hypha {

    private List<Tecton> tectons = new ArrayList<>();
    private Mycologist spicies;
    // public boolean isConnectedToFungus(Hypha h);

    // public void continueHypha(Tecton t);

    // public void continueHypha(Hypha h);
} // End of Hypha
