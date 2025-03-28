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
     * vagy a szomszédos tektonokra, vagy azok szomszédjaira.
     */

    //Itt eredetileg kapott paraméterként egy listát, de kivettem, mert szerintem nem kell
    public void spreadSpores() {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Fungus", "spreadSpores");

        //ha a gomba töltöttsége 2, spórákat szór(hat) a szomszédos tektonokra
        if (charge == 2) {
            for (Tecton t : tecton.getNeighbours()) {
                t.addSpores(species);
            }
            skeleton.log("A gomba spórát szórt a szomszédos tektonokra.");
            skeleton.endMethod();
            charge = 0;
            return;
        }
        //ha a gomba töltöttsége 3 vagy nagyobb, spórát szór a szomszédos tektonok szomszédaira
        if (charge >= 3) {
            for (Tecton t : tecton.getNeighbours()) {
                for (Tecton neighbourOfTecton : t.getNeighbours()) {
                    /* legyen a gombát tartalmazó tekton GT, ennek bármely szomszédja SzGT.
                     * Egy T tekton akkor szomszédja SzGT-nek, ha szerepel SzGT szomszédainak listájában,
                     * és T != GT vagy SzGT.
                     */
                    if (
                        !neighbourOfTecton.equals(t) &&
                        !t.getNeighbours().contains(neighbourOfTecton)
                    ) {
                        neighbourOfTecton.addSpores(species);
                    }
                }
            }
            skeleton.log(
                "A gomba spórát szórt a szomszédos tektonok szomszédjaira."
            );
            skeleton.endMethod();
            charge = 0;
            return;
        }

        skeleton.log("A gomba még nincs feltöltve.");
        skeleton.endMethod();
    }

    /**
     * A függvény hívásakor a gombatest a paraméterként kapott,
     * legfeljebb kételemű listában tárolt tektonokra növeszt gombafonalakat.
     * @param t
     * @return
     */
    public boolean growHypha(List<Tecton> t) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Fungus", "growHypha");

        // Ellenőrizzük, hogy a lista nem üres
        if (t == null || t.isEmpty()) {
            skeleton.log("Nem lehet növeszteni gombafonalat: a lista üres vagy érvénytelen.");
            skeleton.endMethod();
            return false;
        }

        // Ha a lista két elemet tartalmaz, ellenőrizzük, hogy szomszédosak-e
        if (t.size() == 2 && t.get(0) != null && t.get(1) != null) {
            if (!t.get(0).isNeighbor(t.get(1))) {
                skeleton.log("Nem lehet növeszteni gombafonalat: a tektonok nem szomszédosak egymással.");
                skeleton.endMethod();
                return false;
            }
        }

        // Megpróbálunk gombafonalat növeszteni minden tektonra a listában
        for (Tecton tecton : t) {
            // Ellenőrizzük, hogy a tekton érvényes
            if (tecton == null) {
                skeleton.log("Nem lehet növeszteni gombafonalat: egy tekton érvénytelen.");
                continue;  // Ha egy tekton érvénytelen, lépünk a következőre
            }

            // Növesztünk egy új gombafonalat a tektonra
            Hypha newHypha = new Hypha();
            newHypha.continueHypha(tecton); // A fonal folytatása ezen a tektonon
            tecton.addHypha(newHypha); // Hozzáadjuk a fonalat a tektonhoz

            skeleton.log(
                "Gombafonál sikeresen növesztve a tektonon: " +
                tecton.toString()
            );
        }

        skeleton.endMethod();
        return true; // Ha sikerült minden tektonra növeszteni
    }

    public void setSpecies(Mycologist m){
        this.species = m;
    }

    public void setTecton(Tecton t){
        this.tecton = t;
    }

    public int getCharge(){
        return charge;
    }

    public void setCharge(int c){
        this.charge = c;
    }
}

