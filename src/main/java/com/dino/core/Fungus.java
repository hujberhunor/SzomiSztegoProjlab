package com.dino.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.dino.player.Mycologist;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.Tecton;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.dino.util.Skeleton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Egy gombatestet reprezentáló osztály.
 */
public class Fungus implements SerializableEntity {

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
     * Egy egész szám nulla és három között, ami megadja, hogy a gombatest mennyire
     * van feltöltve, és kész-e a spóraszórásra.
     * Értéke körönként automatikusan eggyel nő.
     * Ha kettő, a gombatest a szomszédos tektonokra tud spórákat szórni, ha három,
     * akkor azok szomszédjaira is.
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
        this.lifespan = 5;
        this.hyphas = new ArrayList<>();
        this.spores = new ArrayList<>();
    }

    public Fungus(Mycologist m, Tecton t) {
        this.species = m;
        this.tecton = t;
        this.charge = 0;
        this.lifespan = 5;
        this.hyphas = new ArrayList<>();
        this.spores = new ArrayList<>();
    }

    /**
     * A gombatest spórákat szór a Charge attribútuma értéke szerint
     * vagy a szomszédos tektonokra, vagy azok szomszédjaira.
     */

    // Itt eredetileg kapott paraméterként egy listát, de kivettem, mert szerintem
    // nem kell
    public void spreadSpores() {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Fungus", "spreadSpores");

        HashSet<Tecton> alreadySpread = new HashSet<>();

        // ha a gomba töltöttsége legalább 2, spórákat szór(hat) a szomszédos tektonokra
        if (charge >= 2) {
            for (Tecton t : tecton.getNeighbours()) {
                t.addSpores(species);
                alreadySpread.add(t);
            }
            skeleton.log("A gomba spórát szórt a szomszédos tektonokra.");
        }
        // ha a gomba töltöttsége 3, spórát szór(hat) a szomszédos tektonok szomszédaira
        // is
        if (charge == 3) {
            for (Tecton t : tecton.getNeighbours()) {
                for (Tecton secondDegree : tecton.getNeighbours()) {
                    if (secondDegree != t && !alreadySpread.contains(secondDegree)) {
                        secondDegree.addSpores(species);
                        alreadySpread.add(secondDegree);
                    }
                }
            }
            skeleton.log("A gomba spórát szórt a szomszédos tektonok szomszédjaira.");
        }

        if (charge < 2) {
            skeleton.log("A gomba még nincs feltöltve.");
        }

        skeleton.endMethod();
        charge = 0;
    }

    /**
     * A függvény hívásakor a gombatest a paraméterként kapott,
     * legfeljebb kételemű listában tárolt tektonokra növeszt gombafonalakat.
     * 
     * @param t
     * @return
     */
    public boolean growHypha(List<Tecton> t) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Fungus", "growHypha");

        // Ellenőrizzük, hogy a lista egy vagy két elemet tartalmaz-e
        if (t == null || t.isEmpty() || t.size() > 2 || t.contains(null)) {
            skeleton.log("Nem lehet növeszteni gombafonalat: a lista üres vagy érvénytelen.");
            skeleton.endMethod();
            return false;
        }

        // Ellenőrízzük, hogy a két tekton szomszédos-e egymással és a gomba tektonjával
        if (!t.get(0).isNeighbor(tecton) || !t.get(1).isNeighbor(t.get(1))) {
            skeleton.log("Nem lehet növeszteni gombafonalat: a tektonok nem szomszédosak egymással.");
            skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy az első (nulladik) tektonon van-e spóra, ha kételemű a
        // lista
        if (t.size() == 2) {
            boolean found = false;
            for (Map.Entry<Mycologist, Integer> entry : t.get(0).spores.entrySet()) {
                Mycologist mycologist = entry.getKey();
                int quantity = entry.getValue();

                if (mycologist.equals(species) && quantity > 0) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                skeleton.log("Nem lehet növeszteni hosszú gombafonalat: az első tektonon nincs spóra.");
                skeleton.endMethod();
                return false;
            }
        }

        // Megpróbálunk gombafonalat növeszteni minden tektonra a listában
        // Inicializáljuk az új fonalat
        Hypha newHypha = new Hypha(species, this);
        tecton.addHypha(newHypha);
        hyphas.add(newHypha);
        newHypha.getTectons().add(tecton);

        for (Tecton currTecton : t) {
            // Növesztünk egy új gombafonalat a tektonra
            newHypha.continueHypha(currTecton); // A fonal folytatása ezen a tektonon
            currTecton.addHypha(newHypha); // Hozzáadjuk a fonalat a tektonhoz

            skeleton.log("Gombafonál sikeresen növesztve a tektonon: " + currTecton.toString());
        }

        skeleton.endMethod();
        return true; // Ha sikerült minden tektonra növeszteni
    }

    public Mycologist getSpecies() {
        return species;
    }

    public void setSpecies(Mycologist m) {
        species = m;
    }

    public Tecton getTecton() {
        return tecton;
    }

    public void setTecton(Tecton t) {
        tecton = t;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int c) {
        if (c >= 0 && c <= 3) {
            charge = c;
        }
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

    public List<Hypha> getHyphas() {
        return hyphas;
    }

    @Override
    public JsonObject serialize(ObjectNamer namer, Logger logger) {
        JsonObject obj = new JsonObject();

        // Gombafaj tulajdonos (név)
        obj.addProperty("species", namer.getNameOf(species));

        // Feltöltöttség / életciklus
        obj.addProperty("charge", charge);
        obj.addProperty("lifespan", lifespan);

        // Hypha-k: névként tárolva
        obj.add("hyphas", SerializerUtil.toJsonArray(hyphas, h -> {
            String name = namer.getNameOf(h);
            return new JsonPrimitive(name != null ? name : "unregistered");
        }));

        // Spore-k: teljes objektumként serialize-olva
        obj.add("spores", SerializerUtil.toJsonArray(spores, s -> s.serialize(namer, logger)));

        return obj;
    }

    public static Fungus deserialize(JsonObject obj, ObjectNamer namer, Logger logger) {
        Fungus f = new Fungus();

        // 1. Gombafaj tulajdonos (név alapján vissza)
        String speciesName = obj.get("species").getAsString();
        Object speciesObj = namer.getByName(speciesName);
        if (speciesObj instanceof Mycologist) {
            f.setSpecies((Mycologist) speciesObj);
        } else {
            logger.logError("Fungus", namer.getNameOf(f), "Ismeretlen vagy hibás Mycologist: " + speciesName);
        }

        // 2. Charge + Lifespan
        f.setCharge(obj.get("charge").getAsInt());
        f.setLifespan(obj.get("lifespan").getAsInt());

        // 3. Hypha névlista → skipeljük itt (csak név volt mentve)
        if (obj.has("hyphas")) {
            JsonArray hyphaArray = obj.getAsJsonArray("hyphas");
            List<Hypha> hyphaList = new ArrayList<>();

            for (JsonElement e : hyphaArray) {
                String hyphaName = e.getAsString();
                Object hyphaObj = namer.getByName(hyphaName);
                if (hyphaObj instanceof Hypha) {
                    hyphaList.add((Hypha) hyphaObj);
                } else {
                    logger.logError("Fungus", namer.getNameOf(f), "Ismeretlen vagy hibás Hypha: " + hyphaName);
                }
            }

            f.hyphas = hyphaList;
        }

        // 4. Spórák visszatöltése név alapján
        if (obj.has("spores")) {
            JsonArray sporeArray = obj.getAsJsonArray("spores");
            List<Spore> sporeList = new ArrayList<>();

            for (JsonElement e : sporeArray) {
                String sporeName = e.getAsString();
                Object sporeObj = namer.getByName(sporeName);

                if (sporeObj instanceof Spore) {
                    sporeList.add((Spore) sporeObj);
                } else {
                    logger.logError("Fungus", namer.getNameOf(f), "Ismeretlen vagy hibás Spore: " + sporeName);
                }
            }

            f.spores = sporeList;
        }

        return f;
    }
}
