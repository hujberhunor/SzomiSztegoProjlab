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
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
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

    // Attribútumok
    protected boolean fungiEnabled;
    protected int hyphaLimit;
    protected int hyphaLifespan;
    public double breakChance;
    public int breakCount;
    public List<Hexagon> hexagons;
    public List<Tecton> neighbours;
    public Fungus fungus;
    public List<Insect> insects;
    public Map<Mycologist, Integer> spores;
    public List<Hypha> hyphas;

    /**
     * Alapértelmezett konstruktor
     */
    protected Tecton() {
        this.fungiEnabled = true;
        this.hyphaLimit = 0;
        this.hyphaLifespan = -1;
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
     * Létrehoz egy ugyanolyan típusú új tektont.
     * Minden alosztály felülírja ezt a metódust.
     * @return Új, az aktuálissal megegyező típusú tekton
     */
    public abstract Tecton createCopy();

    /**
     * A tektonon elhelyez egy darab, m gombász gombájából származó spórát.
     * @param m A gombász, akinek a gombájából a spóra származik
     */
    public void addSpores(Mycologist m) {
        //Skeleton skeleton = Skeleton.getInstance();
        //skeleton.startMethod("Tecton", "add spores");

        spores.put(m, spores.getOrDefault(m, 0) + 1);
        //skeleton.log("Spóra elhelyezve");
        //skeleton.log("A tektonon található gombász-spóraszám párok:");
        //spores.forEach((key, value) -> skeleton.log(key + " = " + value));
    }

    /**
     * A tektonról eltávolít egy darab, m gombász gombájából származó spórát, amennyiben lehetséges.
     * @param m A gombász, akinek a gombájából a spóra származik
     */
    public void removeSpores(Mycologist m) {
        //Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Tecton", "remove spores");

        spores.computeIfPresent(m, (key, value ) -> (value > 1) ? value - 1 : null);
        //skeleton.log("Spóra eltávolítva");
        if (!spores.isEmpty()){
            //skeleton.log("A tektonon található gombász-spóraszám párok:");
            //spores.forEach((key, value) -> skeleton.log(key + " = " + value));
        }
        else {
            //skeleton.log("A tektonon nem található spóra");
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
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);
        
        if (a == null || b == null) {
            logger.logError("TECTON", "connectTectons", "Null Tecton connection");
            return;
        }
        
        String aName = registry.getNameOf(a);
        String bName = registry.getNameOf(b);
        
        if (!a.getNeighbours().contains(b)) {
            a.getNeighbours().add(b);
            logger.logChange("TECTON", a, "NEIGHBOURS_ADD", "-", bName);
        }
        
        if (!b.getNeighbours().contains(a)) {
            b.getNeighbours().add(a);
            logger.logChange("TECTON", b, "NEIGHBOURS_ADD", "-", aName);
        }
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
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);
        String currentTectonName = registry.getNameOf(this);
        
        List<Tecton> resultTectons = new ArrayList<>();
        
        // Ha van rajta rovar, nem törhet el a tekton
        if (!insects.isEmpty()) {
            logger.logError("TECTON", currentTectonName, "Nem törhet el: van rajta rovar");
            return resultTectons;
        }
        
        // Ellenőrizzük, hogy a tekton nem csak egy hexagonból áll-e
        if (hexagons.size() <= 1) {
            logger.logError("TECTON", currentTectonName, "Nem törhet el: csak egy hexagonból áll");
            return resultTectons;
        }
        
        // Ha már kétszer tört a tekton, akkor nem törhet újra
        if (breakCount >= 2) {
            logger.logError("TECTON", currentTectonName, "Nem törhet el: már kétszer tört");
            return resultTectons;
        }
        
        // Random törési valószínűség generálása
        boolean shouldBreak = Math.random() * 100 < this.breakChance;
        if (!shouldBreak) {
            logger.logError("TECTON", currentTectonName, "Nem törhet el: valószínűség nem teljesült");
            return resultTectons;
        }
        
        // Két új tekton létrehozása
        Tecton tecton1 = this.createCopy();
        Tecton tecton2 = this.createCopy();
        
        // Regisztráljuk az új tektonokat
        String newName1 = currentTectonName + "_A";
        String newName2 = currentTectonName + "_B";
        
        registry.register(newName1, tecton1);
        registry.register(newName2, tecton2);
        
        // Törési valószínűségek és számláló frissítése
        double newBreakChance = this.breakChance;
        int newBreakCount = this.breakCount + 1;
        
        if (newBreakCount == 1) {
            newBreakChance = this.breakChance / 2;
        } else if (newBreakCount == 2) {
            newBreakChance = 0;
        }
        
        tecton1.breakChance = newBreakChance;
        tecton1.breakCount = newBreakCount;
        tecton2.breakChance = newBreakChance;
        tecton2.breakCount = newBreakCount;
        
        // Hexagonok felosztása és egyéb műveletek
        divideHexagons(tecton1, tecton2);
        
        // Gombafonalak törlése
        tecton1.hyphas = new ArrayList<>();
        tecton2.hyphas = new ArrayList<>();
        
        // Gombatestek véletlenszerű áthelyezése
        if (fungus != null) {
            Tecton targetTecton = (Math.random() < 0.5) ? tecton1 : tecton2;
            targetTecton.fungus = this.fungus;
            logger.logChange("FUNGUS", fungus, "LOCATION", currentTectonName, registry.getNameOf(targetTecton));
        }
        
        // Spórák elosztása
        divideSpores(tecton1, tecton2);
        
        // Logoljuk a sikeres törést
        logger.logChange("TECTON", this, "SPLIT", "-", newName1 + ", " + newName2);
        
        resultTectons.add(tecton1);
        resultTectons.add(tecton2);
        
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

    public void addInsect(Insect insect){ insects.add(insect); }

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

    public double getBreakChance(){
        return breakChance;
    }
    /**
     * Szétosztja a hexagonokat a két új tekton között.
     * Minden hexagon az egyik vagy másik tektonhoz kerül.
     * Az algoritmus a sok implementációs lehetőség közül egy egyszerű
     * megoldást választ: a hexagonokat két részre osztja, de biztosítja,
     * hogy mindkét részben legalább egy hexagon legyen.
     * 
     * @param tecton1 Az első új tekton
     * @param tecton2 A második új tekton
     */
    private void divideHexagons(Tecton tecton1, Tecton tecton2) {
        if (hexagons.size() <= 1) return;
        
        // Hexagonok számának meghatározása az első tekton számára
        int firstTectonHexCount = Math.max(1, hexagons.size() / 2);
        
        tecton1.hexagons = new ArrayList<>(hexagons.subList(0, firstTectonHexCount));
        tecton2.hexagons = new ArrayList<>(hexagons.subList(firstTectonHexCount, hexagons.size()));
    }

    /**
     * Szétosztja a spórákat a két új tekton között.
     * Minden gombász spórája véletlenszerűen kerül az egyik vagy másik tektonra.
     * 
     * @param tecton1 Az első új tekton
     * @param tecton2 A második új tekton
     */
    private void divideSpores(Tecton tecton1, Tecton tecton2) {
        tecton1.spores = new HashMap<>();
        tecton2.spores = new HashMap<>();
        
        for (Map.Entry<Mycologist, Integer> entry : spores.entrySet()) {
            Mycologist mycologist = entry.getKey();
            Integer sporeCount = entry.getValue();
            
            if (sporeCount <= 0) continue;
            
            // Véletlenszerűen választunk a két tekton között
            if (Math.random() < 0.5) {
                tecton1.spores.put(mycologist, sporeCount);
            } else {
                tecton2.spores.put(mycologist, sporeCount);
            }
        }
    }
}
