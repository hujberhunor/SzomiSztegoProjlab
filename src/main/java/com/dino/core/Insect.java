package com.dino.core;

import com.dino.effects.AcceleratingEffect;
import com.dino.effects.CloneEffect;
import com.dino.effects.ParalyzingEffect;
import com.dino.effects.SlowingEffect;
import com.dino.effects.SporeNoEffect;
import com.dino.effects.StunningEffect;
import com.dino.engine.Game;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.dino.util.Skeleton;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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

    private final EntityRegistry registry = EntityRegistry.getInstance();
    private final ObjectNamer namer = ObjectNamer.getInstance();
    private final Logger logger = Logger.getInstance();

    public Insect(Entomologist entomologist, Tecton currentTecton) {
        this.entomologist = entomologist;
        this.currentTecton = currentTecton;
        this.effects = new ArrayList<>();
        this.entomologist.addInsects(this);
    }

    // Számontartja, hogy a rovar mozoghat-e ingyen a jelenlegi körében, ha gyorsító
    // hatás alatt van
    // Értéke minden kör kezdetén igaz, extra mozgás után értéke hamisra változik
    private boolean extraMove;

    /*
     * Copy konstruktor
     * Klónozáshoz kell
     */
    public Insect(Insect original) {
        this.entomologist = original.entomologist;
        this.currentTecton = original.currentTecton;
        this.effects = new ArrayList<>();

        /// Csak azokat az effekteket pakolom bele amelyek nem cloneEffectek (végtelen
        /// rekurzió elkerülése végett)
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
     * @param targetTecton
     * @return true ha a mozgás sikeres volt, különben false
     */
    public boolean move(Tecton targetTecton) {
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Insect", "move");

        // Ellenőrizzük, hogy van-e még akciópont
        if (entomologist.getRemainingActions() <= 0) {
            logger.logError(
                "INSECT",
                namer.getName(this),
                "Nem mozdulhat: nincs több akciópont a körben."
            );
            // skeleton.log("Nem mozdulhat: nincs több akciópont a körben.");
            // skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy bénító hatás alatt van-e
        if (isUnderEffect(ParalyzingEffect.class)) {
            logger.logError(
                "INSECT",
                namer.getName(this),
                "Nem mozdulhat: ParalyzingEffect hatás alatt van."
            );
            // skeleton.log("Nem mozdulhat: ParalyzingEffect hatás alatt van.");
            // skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy a tektonok szomszédosak-e
        if (!currentTecton.isNeighbor(targetTecton)) {
            logger.logError(
                "INSECT",
                namer.getName(this),
                "Nem mozdulhat: a cél tekton nem szomszédos."
            );
            // skeleton.log("Nem mozdulhat: a cél tekton nem szomszédos.");
            // skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy van-e összekötő gombafonál
        if (!currentTecton.hasHypha(targetTecton)) {
            logger.logError(
                "INSECT",
                namer.getName(this),
                "Nem mozdulhat: nincs összekötő gombafonál."
            );
            // skeleton.log("Nem mozdulhat: nincs összekötő gombafonál.");
            // skeleton.endMethod();
            return false;
        }

        // Sikeres mozgás
        logger.logChange("INSECT", this, "TECTON", currentTecton, targetTecton);
        logger.logOk(
            "INSECT",
            namer.getName(this),
            "ACTION",
            "ATTEMPT_MOVE",
            "SUCCESS"
        );

        currentTecton = targetTecton;
        currentTecton.getInsects().remove(this);
        targetTecton.getInsects().add(this);
        // skeleton.log("Rovar sikeresen mozgott az új tektonra.");

        if (isUnderEffect(AcceleratingEffect.class) && extraMove && isUnderEffect(SlowingEffect.class)) {
            setExtraMove(false);
            entomologist.decreaseActions();
        } else if (isUnderEffect(AcceleratingEffect.class) && extraMove) {
            setExtraMove(false);
        } else if (isUnderEffect(SlowingEffect.class)) {
            entomologist.setActions(0);
        } else {
            entomologist.decreaseActions(); // Csökkentjük az akciópontját
        }

        // skeleton.endMethod();
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
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Insect", "cut");
        Game game = Game.getInstance();

        // Ellenőrizzük, hogy a tektonok szomszédosak-e
        if (!currentTecton.isNeighbor(targetTecton)) {
            logger.logError(
                "INSECT",
                namer.getName(this),
                "Nem vághat: a cél tekton nem szomszédos."
            );
            // skeleton.log("Nem vághat: a cél tekton nem szomszédos.");
            // skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy a fonál fut-e a tektonon
        if (!h.getTectons().contains(targetTecton)) {
            logger.logError(
                "INSECT",
                namer.getName(this),
                "Nem vághat: a fonál nem a tektonon fut."
            );
            // skeleton.log("Nem vághat: a fonál nem a tektonon fut.");
            // skeleton.endMethod();
            return false;
        }

        // Ellenőrizzük, hogy a rovar kábító spórák hatása alatt van-e
        if (isUnderEffect(StunningEffect.class)) {
            logger.logError(
                "INSECT",
                namer.getName(this),
                "Nem vághat: a rovar kábító spóra hatása alatt van."
            );
            // skeleton.log("Nem vághat: a rovar kábító spóra hatása alatt van.");
            // skeleton.endMethod();
            return false;
        }

        // Sikeres fonálvágás
        // Kikeressük, hogy a fonál tektonlistájában hol található a tekon, amire a
        // rovar vág
        // A kapott indextől kezdve töröljük a lista elemeit
        h.splitHypha(targetTecton, game);

        int oldTectonCount = h.getTectons().size();
        Tecton last = h.getTectons().get(oldTectonCount - 1);

        logger.logChange("HYPHA", h, "LAST_TECTON", last, h.getTectons().get(h.getTectons().size() - 1));
        logger.logChange("HYPHA", h, "TECTON_COUNT", oldTectonCount, h.getTectons().size());

        // skeleton.log("Rovar sikeresen elvágta a fonalat.");
        logger.logOk("INSECT", namer.getName(this), "ACTION", "ATTEMPT_CUTTING_HYPHA", "SUCCESS");
        entomologist.decreaseActions();
        // skeleton.endMethod();

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
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Insect", "consume");

        // Ellenőrizzük, hogy van-e spóra a tektonon
        if (currentTecton.spores.isEmpty()) {
            logger.logError(
                "INSECT",
                namer.getName(this),
                "Nem lehet spórát enni: a tektonon nincs spóra."
            );
            // skeleton.log("Nem lehet spórát enni: a tektonon nincs spóra.");
            // skeleton.endMethod();
            return false;
        }

        // Spóra elfogyasztása
        Map<Mycologist, Long> sporeCountByMycologist= currentTecton.spores.keySet().stream().collect(Collectors.groupingBy(Spore::getSpecies, Collectors.counting()));

        Optional<Map.Entry<Mycologist, Long>> maxEntry = sporeCountByMycologist.entrySet().stream().max(Map.Entry.comparingByValue());

        maxEntry.ifPresent(entry -> {Mycologist topMycologist = entry.getKey();
        
            Optional<Spore> sporeToRemove = currentTecton.spores.keySet().stream().filter(spore -> spore.getSpecies().equals(topMycologist)).findFirst();

            sporeToRemove.ifPresent(spore -> {
                currentTecton.removeSpores(spore);
                entomologist.increaseScore(spore.getNutrientValue());
                spore.applyTo(this);
            });
        });

        logger.logOk(
            "INSECT",
            namer.getName(this),
            "ACTION",
            "ATTEMPT_EATING_SPORE",
            "SUCCESS"
        );
        //skeleton.log("Rovar sikeresen elfogyasztotta a spórát.");
        //skeleton.endMethod();

        return true;
    }

    /**
     * A rovar effektlistájához hozzáadja az új effektet.
     *
     * @param s
     */
    public void addEffects(Spore s) {
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Insect", "add effects");

        int prevScore = entomologist.score;

        entomologist.score += s.getNutrientValue();
        effects.add(s);

        logger.logChange(
            "ENTOMOLOGIST",
            entomologist,
            "SCORE",
            prevScore,
            entomologist.score
        );
        // skeleton.log("Elfogyasztott spóra: " + s.toString());
        // skeleton.endMethod();
    }

    /**
     * A rovar effektlistájáról eltávolítja a lejárt effektet.
     */
    public void removeExpiredEffects() {
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Insect", "remove effects");

        for (int i = 0; i < effects.size(); i++) {
            if (effects.get(i).getEffectDuration() == 0) {
                logger.logChange("INSECT", this, "REMOVED_EFFECT", "-", i);
                // skeleton.log("Törölt spóra: " + effects.get(i).toString());
                effects.remove(i);
            }
        }
    }

    public boolean isUnderEffect(Class<?> clazz) {
        if (effects != null && !effects.isEmpty()) {
            for (Spore s : effects) {
                if (clazz.isInstance(s)) return true;
            }
        }
        return false;
    }

    /**
     * A rovar eltávolítása a játékból.
     */
    public void destroyInsect() {
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

    public void setExtraMove(boolean b) {
        extraMove = b;
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = new JsonObject();

        obj.addProperty("name", namer.getName(this));
        obj.addProperty("type", "Insect");

        obj.addProperty("owner", namer.getName(entomologist));
        obj.addProperty("currentTecton", namer.getName(currentTecton));

        // Effektek (Spore objektumok) szerializálása
        obj.add(
            "effects",
            SerializerUtil.toJsonArray(effects, s -> s.serialize(namer))
        );

        return obj;
    }

    @Override
    public String toString() {
        String name = namer.getName(this);
        if (name != null) {
            return name;
        }

        // If no name is registered, create a representation with owner and position
        String ownerInfo = (entomologist != null)
            ? entomologist.name
            : "unknown";
        String positionInfo = (currentTecton != null)
            ? currentTecton.toString()
            : "nowhere";

        return "Insect(" + ownerInfo + "@" + positionInfo + ")";
    }
}