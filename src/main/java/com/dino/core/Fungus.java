package com.dino.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.dino.effects.SporeNoEffect;
import com.dino.player.Mycologist;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.Tecton;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.google.gson.JsonObject;

/**
 * Egy gombatestet reprezentáló osztály.
 */
public class Fungus implements SerializableEntity {

    private Mycologist species;
    private Tecton tecton;
    private int charge;
    private int lifespan;
    private List<Hypha> hyphas;
    private List<Spore> spores;

    private final ObjectNamer namer = ObjectNamer.getInstance();
    private final Logger logger = Logger.getInstance();

    public Fungus() {
        this.species = new Mycologist();
        this.tecton = new NoFungiTecton();
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

    public void spreadSpores() {
        if (charge < 2) {
            logger.logError("FUNGUS", namer.getName(this), "A gomba még nincs feltöltve.");
            return;
        }

        HashSet<Tecton> alreadySpread = new HashSet<>();

        if (charge >= 2) {
            for (Tecton t : tecton.getNeighbours()) {
                Spore spore = new SporeNoEffect(species);  // Alap spóra, tetszőlegesen cserélhető
                namer.register(spore);
                t.addSpores(spore);
                spores.add(spore);
                alreadySpread.add(t);
            }
            logger.logOk("FUNGUS", namer.getName(this), "ACTION", "ATTEMPT_SPREAD_SPORE_1", "SUCCESS");
        }

        if (charge == 3) {
            for (Tecton t : tecton.getNeighbours()) {
                for (Tecton secondDegree : t.getNeighbours()) {
                    if (!alreadySpread.contains(secondDegree)) {
                        Spore spore = new SporeNoEffect(species);
                        namer.register(spore);
                        secondDegree.addSpores(spore);
                        spores.add(spore);
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
    }

    public boolean growHypha(List<Tecton> t) {
        if (t == null || t.isEmpty() || t.size() > 2 || t.contains(null)) {
            logger.logError("FUNGUS", namer.getName(this), "Nem lehet növeszteni gombafonalat: a lista üres vagy érvénytelen.");
            return false;
        }

        if (!t.get(0).isNeighbor(tecton) || (t.size() == 2 && !t.get(1).isNeighbor(t.get(0)))) {
            logger.logError("FUNGUS", namer.getName(this), "Nem lehet növeszteni gombafonalat: a tektonok nem szomszédosak.");
            return false;
        }

        if (t.size() == 2) {
            boolean found = false;
            for (Map.Entry<Spore, Integer> entry : t.get(0).getSporeMap().entrySet()) {
                Spore spore = entry.getKey();
                int quantity = entry.getValue();
                if (spore.getSpecies().equals(species) && quantity > 0) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                logger.logError("FUNGUS", namer.getName(this), "Nem lehet növeszteni hosszú gombafonalat: az első tektonon nincs spóra.");
                return false;
            }
        }

        Hypha newHypha = new Hypha(species, this);
        namer.register(newHypha);
        tecton.addHypha(newHypha);
        hyphas.add(newHypha);
        newHypha.getTectons().add(tecton);

        for (Tecton currTecton : t) {
            newHypha.continueHypha(currTecton);
            currTecton.addHypha(newHypha);
            logger.logOk("FUNGUS", namer.getName(this), "ACTION", "ATTEMPT_GROW_HYPHA", "SUCCESS");
        }

        species.decreaseActions();
        return true;
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

    public List<Hypha> getHyphas() {
        return hyphas;
    }

    public void setCharge(int c) {
        if (c >= 0 && c <= 3) {
            int prevCharge = charge;
            charge = c;
            logger.logChange("FUNGUS", this, "CHARGE", String.valueOf(prevCharge), charge);
        }
    }

    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }

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
