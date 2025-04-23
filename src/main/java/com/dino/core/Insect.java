package com.dino.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.dino.effects.AcceleratingEffect;
import com.dino.effects.CloneEffect;
import com.dino.effects.ParalyzingEffect;
import com.dino.effects.SlowingEffect;
import com.dino.effects.SporeNoEffect;
import com.dino.effects.StunningEffect;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.dino.util.Skeleton;
import com.google.gson.JsonObject;

/**
 * Egy rovart reprezentáló osztály.
 */
public class Insect implements SerializableEntity {

    /**
     * Megadja, hogy a rovar melyik rovarászhoz tartozik.
     */
    private Entomologist entomologist;
    /**
     * Az a tekton objektum, amin a rovar tartózkodik.
     */
    private Tecton currentTecton;
    /**
     * Azoknak az effekteknek a listája, amik alatt a rovar van.
     * Egy ilyen hatást egy spóra definiál.
     */
    private List<Spore> effects;

    public Insect(Entomologist entomologist, Tecton currentTecton) {
        this.entomologist = entomologist;
        this.currentTecton = currentTecton;
        this.effects = new ArrayList<>();
    }

    /*
     * Copy konstruktor
     * Klónozáshoz kell
     */
    public Insect(Insect original) {
        this.entomologist = original.entomologist;
        this.currentTecton = original.currentTecton;
        this.effects = new ArrayList<>();

        /// Csak azokat az effekteket pakolom bele amelyek nem cloneEffectek (végtelen rekurzió elkerülése végett)
        for (Spore s : original.effects) {
            if (!(s instanceof CloneEffect)) {
                this.effects.add(s);
            }
        }
    }

    /**
     * A rovar átlép a paraméterként kapott tektonra, amennyiben az szomszédos, és
     * vezet át gombafonál a jelenlegi tartózkodási hely és a célként választott
     * tekton között.
     * Visszaadja, hogy sikeres volt-e a művelet.
     * 
     * @param t
     * @return true ha a mozgás sikeres volt, különben false
     */
    public boolean move(Tecton targetTecton) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Insect", "move");

        // Ellenőrizzük, hogy van-e még akciópont
        if (entomologist.getRemainingActions() <= 0) {
            skeleton.log("Nem mozdulhat: nincs több akciópont a körben.");
            skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy bénító hatás alatt van-e
        for (Spore s : effects) {
            if (s instanceof ParalyzingEffect) {
                skeleton.log(
                        "Nem mozdulhat: ParalyzingEffect hatás alatt van.");
                skeleton.endMethod();
                return false;
            }
        }

        // Ellenőrizzük, hogy a tektonok szomszédosak-e
        if (!currentTecton.isNeighbor(targetTecton)) {
            skeleton.log("Nem mozdulhat: a cél tekton nem szomszédos.");
            skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy van-e összekötő gombafonál
        if (!currentTecton.hasHypha(targetTecton)) {
            skeleton.log("Nem mozdulhat: nincs összekötő gombafonál.");
            skeleton.endMethod();
            return false;
        }

        // Sikeres mozgás
        currentTecton = targetTecton;
        skeleton.log("Rovar sikeresen mozgott az új tektonra.");
        entomologist.decreaseActions(); // Csökkentjük az akciópontját
        skeleton.endMethod();
        return true;
    }

    /**
     * A rovar megsemmisíti a választott tekton és a jelenlegi tartózkodási helye
     * között futó h fonalat, amennyiben az szomszédos az övével.
     * Visszaadja, hogy sikeres volt-e a művelet.
     * 
     * @param h
     * @param targetTecton
     * @return
     */
    public boolean cutHypha(Hypha h, Tecton targetTecton) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Insect", "cut");

        // Ellenőrizzük, hogy a tektonok szomszédosak-e
        if (!currentTecton.isNeighbor(targetTecton)) {
            skeleton.log("Nem vághat: a cél tekton nem szomszédos.");
            skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy a fonál fut-e a tektonon
        if (!h.getTectons().contains(targetTecton)) {
            skeleton.log("Nem vághat: a fonál nem a tektonon fut.");
            skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy a rovar kábító spórák hatása alatt van-e
        boolean isStunned = effects
                .stream()
                .anyMatch(spore -> spore instanceof StunningEffect);

        if (isStunned) {
            skeleton.log("Nem vághat: a rovar kábító spóra hatása alatt van.");
            skeleton.endMethod();
            return false;
        }

        // Sikeres fonálvágás
        // Kikeressük, hogy a fonál tektonlistájában hol található a tekon, amire a
        // rovar vág
        // A kapott indextől kezdve töröljük a lista elemeit
        int index = -1;

        skeleton.log("A fonál tektonjai a vágás előtt:");
        for (int i = 0; i < h.getTectons().size(); i++) {
            skeleton.log(h.getTectons().get(i).toString());
            if (h.getTectons().get(i).equals(targetTecton)) {
                index = i;
            }
        }

        h.getTectons().subList(index, h.getTectons().size()).clear();

        skeleton.log("A fonál tektonjai a vágás után:");
        for (int i = 0; i < h.getTectons().size(); i++) {
            skeleton.log(h.getTectons().get(i).toString());
        }

        skeleton.log("Rovar sikeresen elvágta a fonalat.");
        entomologist.decreaseActions();
        skeleton.endMethod();

        return true;
    }

    /**
     * A rovar táplálkozik a saját tektonján lévő spórákból, és a tápanyagtartalmat
     * hozzáadja a paraméterként kapott rovarász pontszámához.
     * Ha több fajta található, abból eszik, amelyikből több van (ha egyenlő, akkor
     * véletlenszerű).
     * Ekkor az elfogyasztott spóra tápanyagtartalmától függően nő a játékos
     * pontszáma.
     * 
     * @param e
     */
    public boolean consumeSpores(Entomologist e) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Insect", "consume");

        // Ellenőrizzük, hogy van-e spóra a tektonon
        if (currentTecton.spores.isEmpty()) {
            skeleton.log("Nem lehet spórát enni: a tektonon nincs spóra.");
            skeleton.endMethod();
            return false;
        }

        // Spóra elfogyasztása
        final Mycologist[] mycologistWrapper = new Mycologist[1];

        Optional<Map.Entry<Mycologist, Integer>> maxSporeCountEntry = currentTecton.spores
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());
        maxSporeCountEntry.ifPresent(entry -> {
            mycologistWrapper[0] = entry.getKey();
            currentTecton.removeSpores(mycologistWrapper[0]);
        });

        List<Class<? extends Spore>> sporeClasses = Arrays.asList(
                AcceleratingEffect.class,
                ParalyzingEffect.class,
                SlowingEffect.class,
                SporeNoEffect.class,
                StunningEffect.class);
        Random random = new Random();
        Class<? extends Spore> randomSporeClass = sporeClasses.get(
                random.nextInt(sporeClasses.size()));

        try {
            Spore spore = randomSporeClass
                    .getDeclaredConstructor(Mycologist.class)
                    .newInstance(mycologistWrapper[0]);
            spore.applyTo(this);
        } catch (Exception exc) {
            throw new RuntimeException("Failed to create spore instance", exc);
        }

        skeleton.log("Rovar sikeresen elfogyasztotta a spórát.");
        entomologist.decreaseActions();
        skeleton.endMethod();

        return true;
    }

    /**
     * A rovar effektlistájához hozzáadja az új effektet.
     * 
     * @param s
     */
    public void addEffects(Spore s) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Insect", "add effects");

        entomologist.score += s.getNutrientValue();
        effects.add(s);
        skeleton.log("Elfogyasztott spóra: " + s.toString());
    }

    /**
     * A rovar effektlistájáról eltávolítja a lejárt effektet.
     */
    public void removeExpiredEffects() {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("Insect", "remove effects");

        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i).getEffectDuration() == 0) {
                skeleton.log("Törölt spóra: " + effects.get(i).toString());
                effects.remove(i);
            }
        }
    }

    /**
     * Visszaadja, hogy a rovar le van-e bénulva?
     */
    public boolean isParalyzed(){
        if (effects != null && !effects.isEmpty()){
            for (Spore s : effects){
                if (s.sporeType() == 3)
                    return true;
            }
        }
        return false;
    }

    /**
     * A rovar eltávolítása a játékból.
     */
    public void destroyInsect(){
        currentTecton.getInsects().remove(this);
        entomologist.getInsects().remove(this);
    }

    public List<Spore> getEffects() {
        return effects;
    }

    public Entomologist getEntomologist() {
        return entomologist;
    }

    public Tecton getTecton() {
        return currentTecton;
    }

@Override
    public JsonObject serialize() {
        JsonObject obj = new JsonObject();

        // Rovarász ID
        obj.addProperty("owner", "entomologist_" + entomologist.hashCode());

        // Jelenlegi tecton ID
        obj.addProperty("currentTecton", "tecton_" + currentTecton.hashCode());

        // Aktív effektek (spórák)
        obj.add("effects", SerializerUtil.toJsonArray(effects, s -> s.serialize()));

        return obj;
    }
}
