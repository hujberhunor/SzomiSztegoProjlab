package com.dino.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.dino.effects.AcceleratingEffect;
import com.dino.effects.CloneEffect;
import com.dino.effects.ParalyzingEffect;
import com.dino.effects.SlowingEffect;
import com.dino.effects.SporeNoEffect;
import com.dino.effects.StunningEffect;
import com.dino.player.Mycologist;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.SingleHyphaTecton;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.google.gson.JsonObject;

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

    private final EntityRegistry registry = EntityRegistry.getInstance();
    private final ObjectNamer namer = ObjectNamer.getInstance();
    private final Logger logger = Logger.getInstance();

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

    public Spore createRandomSpore(){
        final Mycologist[] mycologistWrapper = new Mycologist[1];
        mycologistWrapper[0] = species;

        List<Class<? extends Spore>> sporeClasses = Arrays.asList(
            AcceleratingEffect.class,
            ParalyzingEffect.class,
            SlowingEffect.class,
            SporeNoEffect.class,
            StunningEffect.class,
            CloneEffect.class
        );
        Random random = new Random();
        Class<? extends Spore> randomSporeClass = sporeClasses.get(
            random.nextInt(sporeClasses.size())
        );

        try {
            return randomSporeClass
                .getDeclaredConstructor(Mycologist.class)
                .newInstance(mycologistWrapper[0]);
        } catch (Exception exc) {
            throw new RuntimeException("Failed to create spore instance", exc);
        }
    }

    // Itt eredetileg kapott paraméterként egy listát, de kivettem, mert szerintem
    // nem kell
    public void spreadSpores() {
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Fungus", "spreadSpores");

        if (charge < 2) {
            logger.logError("FUNGUS", namer.getName(this), "A gomba még nincs feltöltve.");
            // skeleton.log("A gomba még nincs feltöltve.");
            // skeleton.endMethod();
            return;
        }

        HashSet<Tecton> alreadySpread = new HashSet<>();

        // ha a gomba töltöttsége legalább 2, spórákat szór(hat) a szomszédos tektonokra
        if (charge >= 2) {
            for (Tecton t : tecton.getNeighbours()) {
                t.addSpores(createRandomSpore());
                alreadySpread.add(t);
            }
            logger.logOk("FUNGUS", namer.getName(this), "ACTION", "ATTEMPT_SPREAD_SPORE_1", "SUCCESS");
        }
        // ha a gomba töltöttsége 3, spórát szór(hat) a szomszédos tektonok szomszédaira is
        if (charge == 3) {
            for (Tecton t : tecton.getNeighbours()) {
                for (Tecton secondDegree : t.getNeighbours()) {
                    if (secondDegree != t && !alreadySpread.contains(secondDegree)) {
                        secondDegree.addSpores(createRandomSpore());
                        alreadySpread.add(secondDegree);
                    }
                }
            }
            logger.logOk("FUNGUS", namer.getName(this), "ACTION", "ATTEMPT_SPREAD_SPORE_2", "SUCCESS");
        }

        int prevLifespan = lifespan;
        lifespan--;

        species.decreaseActions();

        int prevCharge = charge;
        charge = 0;

        logger.logChange("FUNGUS", this, "LIFESPAN", String.valueOf(prevLifespan), String.valueOf(lifespan));
        logger.logChange("FUNGUS", this, "CHARGE", String.valueOf(prevCharge), String.valueOf(charge));

        //skeleton.endMethod();
    }

    /**
     * A függvény hívásakor a gombatest a paraméterként kapott,
     * legfeljebb kételemű listában tárolt tektonokra növeszt gombafonalakat.
     * 
     * @param t
     * @return
     */
    public boolean growHypha(List<Tecton> t) {
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Fungus", "growHypha");

        // Ellenőrizzük, hogy a lista egy vagy két elemet tartalmaz-e
        if (t == null || t.isEmpty() || t.size() > 2 || t.contains(null)) {
            logger.logError("FUNGUS", namer.getName(this),"Nem lehet növeszteni gombafonalat: a lista üres vagy érvénytelen.");
            // skeleton.log("Nem lehet növeszteni gombafonalat: a lista üres vagy érvénytelen.");
            // skeleton.endMethod();
            return false;
        }

        // Ellenőrízzük, hogy a két tekton szomszédos-e egymással és a gomba tektonjával
        if (!t.get(0).isNeighbor(t.get(1)) || !t.get(1).isNeighbor(t.get(0))){
            logger.logError("FUNGUS", namer.getName(this),"Nem lehet növeszteni gombafonalat: a tektonok nem szomszédosak egymással.");
            // skeleton.log("Nem lehet növeszteni gombafonalat: a tektonok nem szomszédosak egymással.");
            // skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy az első (nulladik) tektonon van-e spóra, ha kételemű a lista
        if (t.size() == 2){
            boolean found = false;
            for (Map.Entry<Spore, Integer> entry : t.get(0).spores.entrySet()) {
                Spore spore = entry.getKey();
                int quantity = entry.getValue();

                if (spore.getSpecies().equals(species) && quantity > 0) {
                    found = true;
                    break;
                }
            }
            if (!found){
                logger.logError("FUNGUS", namer.getName(this),"Nem lehet növeszteni hosszú gombafonalat: az első tektonon nincs spóra.");
                // skeleton.log("Nem lehet növeszteni hosszú gombafonalat: az első tektonon nincs spóra.");
                // skeleton.endMethod();
            }
        }

        // Megpróbálunk gombafonalat növeszteni minden tektonra a listában
        // Inicializáljuk az új fonalat
        Hypha newHypha = new Hypha(species, this);
        namer.register(newHypha);
        tecton.addHypha(newHypha);
        t.get(1).addHypha(newHypha);
        hyphas.add(newHypha);
        newHypha.getTectons().add(tecton);
        newHypha.getTectons().add(t.get(1));

        for (Tecton currTecton : t) {
            /*
            // Növesztünk egy új gombafonalat a tektonra
            newHypha.continueHypha(currTecton); // A fonal folytatása ezen a tektonon
            currTecton.addHypha(newHypha); // Hozzáadjuk a fonalat a tektonhoz
             */

            logger.logOk("FUNGUS", namer.getName(this),"ACTION", "ATTEMPT_GROW_HYPHA", "SUCCESS");
            // skeleton.log("Gombafonál sikeresen növesztve a tektonon: " + currTecton.toString());
        }

        // skeleton.endMethod();
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
  
    public List<Spore> getSpores() {
        return spores;
    }

    public List<Hypha> getHyphas(){
        return hyphas;
    }

    public void setCharge(int c){
        if (c >= 0 && c <= 3){
            int prevCharge = charge;
            charge = c;
            logger.logChange("FUNGUS", this, "CHARGE", String.valueOf(prevCharge), charge);
        }
    }

    public void increaseCharge(){ charge += 1; }

    public int getLifespan() {  return lifespan; }
    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }
    public void decreaseLifespan() { lifespan--; }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", namer.getName(this));
        obj.addProperty("type", "Fungus");

        obj.addProperty("species", namer.getName(species));
        obj.addProperty("charge", charge);
        obj.addProperty("lifespan", lifespan);
        obj.addProperty("tecton", namer.getName(tecton));

        obj.add("hyphas", SerializerUtil.toJsonArray(hyphas, h -> h.serialize(namer)));
        obj.add("spores", SerializerUtil.toJsonArray(spores, s -> s.serialize(namer)));

        return obj;
    }

}