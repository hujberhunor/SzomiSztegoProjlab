package com.dino.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.dino.effects.ParalyzingEffect;
import com.dino.engine.Game;
import com.dino.player.Mycologist;
import com.dino.tecton.InfiniteHyphaTecton;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.google.gson.JsonObject;

/**
 * A gombafonalakat reprezentáló osztály. Egy objektum a növesztés sorrendjében
 * tartalmazza a tektonokat,
 * amiken keresztül nő, hogy a fonál elszakadása esetén (rovar vagy törés
 * hatására) a szekvenciából egyértelmű
 * legyen, hogy a fonál melyik fele nem kapcsolódik már a gombatestből, amiből
 * származnak.
 */
public class Hypha implements SerializableEntity {

    /**
     * Azon tectonok amelyeken keresztül halad a fonal
     */
    private List<Tecton> tectons;

    /**
     * Amely gombászhoz tartozik a fonal
     */
    private Mycologist mycologist;
    /**
     * Source fungus, ahonnan nő a fonal. 0. eleme a tecton listának
     */
    private Fungus fungus;

    private int lifespan = 4;

    private final EntityRegistry registry = EntityRegistry.getInstance();
    private final ObjectNamer namer = ObjectNamer.getInstance();
    private final Logger logger = Logger.getInstance();

    public Hypha() {
        tectons = new ArrayList<>();
    }

    public Hypha(Mycologist m, Fungus f) {
        tectons = new ArrayList<>();
        mycologist = m; // Kinek a gombájáról
        fungus = f; // source fungus, ahonna indul a fonal
    }

    /**
     * Visszaadja, hogy mely gombászhoz tartozik a fonal
     *
     * @return A fonalhoz tatozó gombász
     */
    public Mycologist getMycologist() {
        return mycologist;
    }

    /**
     * Beállítja, hogy mely gombászhoz tatozzon a fonal
     *
     * @param species Gombász, akihez beállítódik a fonal
     */
    public void setMychologist(Mycologist m) {
        mycologist = m;
    }

    /**
     * Visszaadja, hogy mely gombához tartozik a fonal
     *
     * @return A fonalhoz tatozó gomba
     */
    public Fungus getFungus() {
        return fungus;
    }

    /**
     * Beállítja, hogy mely gombához tatozzon a fonal
     *
     * @param f Gomba, akihez beállítódik a fonal
     */
    public void setFungus(Fungus f) {
        fungus = f;
    }

    public int getLifespan() {
        return lifespan;
    }

    public void setLifespan(int i) {
        lifespan = i;
    }

    /**
     * Folytatja a már megkeztedd fonalat. Hozzáad "egy tectonnyi fonalat" a lista
     * végére
     */
    public boolean continueHypha(Tecton t) {
        if (tectons.isEmpty()) {
            tectons.add(t);
            t.addHypha(this);
            logger.logChange(
                    "HYPHA",
                    namer.getName(this),
                    "LAST_TECTON",
                    "-",
                    namer.getName(t));
            logger.logOk(
                    "HYPHA",
                    namer.getName(this),
                    "ACTION",
                    "ATTEMPT_CONTINUE_HYPA",
                    "SUCCESS");
            return true;
        }

        Tecton last = tectons.get(tectons.size() - 1);
        if (last.isNeighbor(t)) {
            tectons.add(t);
            t.addHypha(this); // 👈 NÉLKÜLE NINCS GUI
            logger.logChange(
                    "HYPHA",
                    namer.getName(this),
                    "LAST_TECTON",
                    namer.getName(last),
                    namer.getName(t));
            logger.logOk(
                    "HYPHA",
                    namer.getName(this),
                    "ACTION",
                    "ATTEMPT_CONTINUE_HYPA",
                    "SUCCESS");
            return true;
        }

        logger.logError(
                "HYPHA",
                namer.getName(this),
                "A fonálnövesztés sikertelen.");
        return false;
    }

    /**
     * Fonal haladásának tektonjai, konkrétan maga a fonal
     */
    public List<Tecton> getTectons() {
        return tectons;
    }

    public void connectTectons(Tecton... path) {
        Collections.addAll(tectons, path);
    }

    public boolean eatInsect(Insect i) {
        // Megnézzük, hogy a rovar rajta van-e az egyik olyan tektonon, amin fut a fonál
        Tecton targetTecton = null;
        for (Tecton t : tectons) {
            if (i.getTecton().equals(t) && i.isUnderEffect(ParalyzingEffect.class)) {
                targetTecton = t;
                break;
            }
        }

        if (targetTecton == null) {
            logger.logError(
                    "HYPHA",
                    namer.getName(this),
                    "A rovar nem olyan tektonon van, amin fut fonál.");
            return false;
        }

        // A rovart eltűntetjük a céltektonról, létrehozunk egy új gombát
        i.destroyInsect();
        mycologist.placeFungus(targetTecton);
        logger.logOk(
                "HYPHA",
                namer.getName(this),
                "ACTION",
                "ATTEMPT_EAT_INSECT",
                "SUCCESS");
        return true;
    }

    // Elpusztít egy fonalat - törli mindehonnan, ahol számon van tartva
    public void destroyHypha() {
        for (Tecton t : tectons) {
            if (t.getHyphas().contains(this)) {
                t.getHyphas().remove(this);
            }
        }
        fungus.getHyphas().remove(this);
        Game game = Game.getInstance();
        game.getDecayedHyphas().remove(this);
    }

    // Visszaadja, hogy egy fonál fut-e át InfiniteHyphaTecton-on
    public boolean containsInfiniteTecton() {
        for (Tecton t : tectons) {
            if (t instanceof InfiniteHyphaTecton) {
                return true;
            }
        }
        return false;
    }

    // Kettlvág egy fonalat - a második felét decay-eli, ha kell
    public void splitHypha(Tecton tecton, Game game) {
        int index = -1;
        for (int i = 0; i < tectons.size(); i++) {
            if (tectons.get(i).equals(tecton)) {
                index = i;
                break;
            }
        }
        Hypha newHypha = new Hypha(mycologist, fungus);
        namer.register(newHypha);
        for (Tecton t : tectons.subList(index, tectons.size())) {
            newHypha.getTectons().add(t);
            t.getHyphas().add(newHypha);
            t.getHyphas().remove(this);
        }

        tectons.subList(index, tectons.size()).clear();

        if (!newHypha.containsInfiniteTecton()) {
            game.addDecayedHypha(newHypha);
        }
        lifespan = 4;

        logger.logChange("HYPHA", newHypha, "STATUS", "ACTIVE", "DECAYED");
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", namer.getName(this));
        obj.addProperty("type", "Hypha");

        obj.addProperty("mycologist", namer.getName(mycologist));
        obj.addProperty("fungus", namer.getName(fungus));
        obj.addProperty("lifespan", lifespan);

        obj.add("tectons", SerializerUtil.toJsonArray(tectons, namer::getName));

        return obj;
    }

    @Override
    public String toString() {
        String name = namer.getName(this);
        if (name != null) {
            return name;
        }

        // If no name is registered, create a representation based on tectons
        if (tectons != null && !tectons.isEmpty()) {
            return ("Hypha(" +
                    tectons
                            .stream()
                            .map(t -> t.toString())
                            .collect(Collectors.joining("→"))
                    +
                    ")");
        }

        // Fallback to default
        return "Hypha@" + Integer.toHexString(System.identityHashCode(this));
    }
} // End of Hypha