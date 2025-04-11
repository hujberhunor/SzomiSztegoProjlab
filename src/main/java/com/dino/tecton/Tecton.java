package com.dino.tecton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dino.core.Fungus;
import com.dino.core.Hexagon;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.player.Mycologist;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.dino.util.Skeleton;
import com.google.gson.JsonObject;

/**
 * Ennek az absztrakt osztálynak a leszármazottjai reprezentálják a játékteret alkotó egységeket, vagy mezőket.
 * Tárolja a mezőket alkotó kisebb hatszögeket, illetve az egész egységre vonatkozó tulajdonságokat és annak állapotát.
 * A tekton altípusoknak az ősosztálya.
 */
public abstract class Tecton implements SerializableEntity {

    /// Attribútumok
    // protected boolean fungiEnabled;
    // protected int hyphaLimit;
    // protected int hyphaLifespan;
    public double breakChance;
    public int breakCount;
    public List<Hexagon> hexagons;
    public List<Tecton> neighbours;
    public Fungus fungus;
    public List<Insect> insects;
    public Map<Mycologist, Integer> spores;
    // TODO ÁTÍRNI 
    // publicted List<Spores> spores;
    public List<Hypha> hyphas;

    /**
     * Alapértelmezett konstruktor
     */
    public Tecton() {
        // this.fungiEnabled = true;
        // this.hyphaLimit = 0;
        // this.hyphaLifespan = -1;
        this.breakChance = 5.0 + Math.random() * 35.0;
        this.breakCount = 0;
        this.hexagons = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.fungus = null;
        this.insects = new ArrayList<>();
        this.spores = new HashMap<>();
        this.hyphas = new ArrayList<>();
    }

    public List<Hypha> getHyphas() {
        return this.hyphas;
    }

    public void setHyphas(List<Hypha> hyphas){
        this.hyphas = hyphas;
    }

    public List<Tecton> getNeighbours() {
        return neighbours;
    }

    /**
     * A tektonon elhelyez egy darab, m gombász gombájából származó spórát.
     * @param m A gombász, akinek a gombájából a spóra származik
     */
    public void addSpores(Mycologist m) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Tecton", "add spores");

        spores.put(m, spores.getOrDefault(m, 0) + 1);
        skeleton.log("Spóra elhelyezve");
        skeleton.log("A tektonon található gombász-spóraszám párok:");
        spores.forEach((key, value) -> skeleton.log(key + " = " + value));
    }

    /**
     * A tektonról eltávolít egy darab, m gombász gombájából származó spórát, amennyiben lehetséges.
     * @param m A gombász, akinek a gombájából a spóra származik
     */
    public void removeSpores(Mycologist m) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Tecton", "remove spores");

        spores.computeIfPresent(m, (key, value ) -> (value > 1) ? value - 1 : null);
        skeleton.log("Spóra eltávolítva");
        if (!spores.isEmpty()){
            skeleton.log("A tektonon található gombász-spóraszám párok:");
            spores.forEach((key, value) -> skeleton.log(key + " = " + value));
        }
        else {
            skeleton.log("A tektonon nem található spóra");
        }
    }

    /**
     * A tektonon elhelyezi a paraméterként kapott gombafonalat.
     * A Hypha osztály tárolja, hogy melyik gombász gombájától származik.
     *
     * @param h Az elhelyezendő gombafonál
     */
    public void addHypha(Hypha h) {
        hyphas.add(h);
    }


    /**
     * Kétirányú asszociáció miatt delegáltam fv-be
     */
    public static void connectTectons(Tecton a, Tecton b) {
        a.getNeighbours().add(b);
        b.getNeighbours().add(a);
    }

    /**
     * Kétirányú asszociáció miatt kell
     * Nagyon random de elv működik.
     */
    public static Hypha connectWithHypha(Tecton... tectons) {
        Hypha hypha = new Hypha();
        for (Tecton t : tectons) {
            hypha.getTectons().add(t);
            t.getHyphas().add(hypha);
        }
        return hypha;
    }

    /**
     * Minden kör után hívódó függvény, ami breakChance eséllyel, hatszögek mentén létrehoz két új tektont
     * a saját helyén, míg magát megszűnteti. Az új tektonok tulajdonságai egyeznek az őket létrehozó tulajdonságaival,
     * amiről a gombatest és rovarok véletlenszerűen választott töredékekre kerülnek át, a gombafonalak pedig megsemmisülnek.
     * Visszaadja a két új tektont listaként.
     *
     * @param breakChance Törési esély
     * @return Az újonnan létrehozott két tekton listája
     */
    public List<Tecton> split(double breakChance) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Tecton", "split");

        List<Tecton> resultTectons = new ArrayList<>();

        // Ha van rajta rovar, nem törhet el a tekton
        if (insects.size() > 0) {
            skeleton.log("A tekton nem törhet el, mert van rajta rovar");
            skeleton.endMethod();
            return resultTectons;
        }

        // Ellenőrizzük, hogy a tekton nem csak egy hexagonból áll-e
        if (hexagons.size() == 1) {
            skeleton.log(
                "A tekton nem törhet el, mert csak egy hexagonból áll"
            );
            skeleton.endMethod();
            return resultTectons; // Üres lista
        }

        // Törési valószínűség megjelenítése
        skeleton.log("Törési valószínűség: " + this.breakChance + "%");

        // Random törési valószínűség generálásának szimulálása
        boolean shouldBreak = Math.random() * 100 < this.breakChance;
        if (!shouldBreak) {
            skeleton.log("Valószínűség miatt nincs törés");
            skeleton.endMethod();
            return resultTectons;
        }

        // A tekton ketté törése
        skeleton.log("A tekton kettétört");

        //Két új tekton létrehozása a törés után
        skeleton.log(
            "Két új tekton jön létre,a típusuk megegyezik az eredetivel"
        );

        Tecton tecton1 = null;
        Tecton tecton2 = null;

        skeleton.log("Hexagonok felosztása a két új tekton között");

        // Törési valószínűségek frissítése
        if (breakCount == 0) {
            skeleton.log(
                "Első törés - törési valószínűség felezve: " +
                (this.breakChance / 2) +
                "%"
            );
            skeleton.log("breakCount növelése 1-re");
        } else if (breakCount == 1) {
            skeleton.log("Második törés - törési valószínűség nullázva");
            skeleton.log("breakCount növelése 2-re");
        }

        // Gombafonalak törlése
        skeleton.log("Gombafonalak törlése az eredeti tektonról");

        // Gombatestek véletlenszerű áthelyezése
        if (fungus != null) {
            skeleton.log(
                "Gombatest véletlenszerűen áthelyezve az egyik új tektonra"
            );
        }

        // Spórák elosztása
        skeleton.log("Spórák elosztása az új tektonok között");

        // Tekton törés befejezése
        skeleton.log("Tekton sikeresen ketté tört");

        skeleton.log("Két új tekton visszaadása az eredményben");

        resultTectons.add(tecton1);
        resultTectons.add(tecton2);

        skeleton.endMethod();
        return resultTectons;
    }

    /**
     * A fonalakat kezelő virtuális függvény, amelyet az alosztályok felülírnak.
     *
     * @param h A kezelendő gombafonál
     */
    public abstract void handleHypha(Hypha h);

    public boolean isNeighbor(Tecton t) {
        return neighbours.contains(t);
    }

    /**
     * Ellenőrzi, hogy az aktuális tekton és a paraméterként megadott tekton
     * között van-e gombafonál összeköttetés.
     * Elsősorban a rovar szempontjából fontos, a fonal vágás akciójához.
     * @param t A tekton, amellyel az összeköttetést ellenőrizzük
     * @return Igaz, ha van gombafonál a két tekton között, egyébként hamis
     */
    public boolean hasHypha(Tecton t) {
        for (Hypha h : hyphas) {
            List<Tecton> path = h.getTectons();
            for (int i = 0; i < path.size() - 1; i++) {
                Tecton a = path.get(i);
                Tecton b = path.get(i + 1);
                if ((a == this && b == t) || (a == t && b == this)) {
                    return true;
            }
        }
    }
    return false;
}


    /**
     * Ellenőrzi, hogy a tektonon van-e a megadott gombásztól származó spóra.
     * @param m A gombász, akinek a spóráját keressük.
     * @return Igaz, ha van legalább egy spóra a gombásztól, egyébként hamis.
     */
    public boolean hasSpores(Mycologist m) {
        return spores.getOrDefault(m, 0) > 0;
    }

    /**
     * Visszaadja a tektonon lévő gombatestet
     * @return A tektonon lévő gombatest
     */
    public Fungus getFungus(){
        return fungus;
    }

    /**
     * Beállítja a tektonon lévő gombatestet.
     * @param f Az új gombatest, amelyet elhelyezünk a tektonon.
     */
    public void setFungus(Fungus f) {
        this.fungus = f;
    }

    public void setNeighbours(List<Tecton> t){
        this.neighbours = t;
    }

    public List<Insect> getInsects(){ 
        return insects;
    }

    @Override
public JsonObject serialize() {
    JsonObject obj = new JsonObject();

    obj.addProperty("id", "tecton_" + hashCode());
    obj.addProperty("breakChance", breakChance);
    obj.addProperty("breakCount", breakCount);

    // Hexagonok ID-val
    obj.add("hexagons", SerializerUtil.toJsonArray(hexagons, Hexagon::getId));

    // Neighbour Tecton ID-val
    obj.add("neighbours", SerializerUtil.toJsonArray(neighbours, t -> "tecton_" + t.hashCode()));

    // Fungus serialize
    if (fungus != null) {
        obj.add("fungus", fungus.serialize());
    }

    // Insect serialize
    obj.add("insects", SerializerUtil.toJsonArray(insects, Insect::serialize));

    // Spore Map serialize
    obj.add("spores", SerializerUtil.toJsonMap(spores, m -> "mycologist_" + m.hashCode()));

    // Hypha serialize
    obj.add("hyphas", SerializerUtil.toJsonArray(hyphas, Hypha::serialize));

    return obj;
}

}
