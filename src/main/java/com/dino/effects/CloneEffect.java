package com.dino.effects;

import java.util.List;

import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.google.gson.JsonObject;

/*
 * Olyan spóra hatás amely az őt elfogyasztó rovart klónozza az elfogyasztásnak helyet adó tektonon
 */
public class CloneEffect extends Spore {

    private static final int CLONE_EFFECT_NUTRIENT_VALUE = 1;

    // Default konstruktor, beállítja a tápanyagtartalom értékét.
    public CloneEffect(Mycologist mycologist) {
        super(mycologist, CLONE_EFFECT_NUTRIENT_VALUE);
    }

    @Override
    public int getNutrientValue() {
        return CLONE_EFFECT_NUTRIENT_VALUE;
    }

    public String toString() {
        return "Clone Spore";
    }

    @Override
    public int sporeType() {
        return 2;
    }

    /*
     * A gomba hatását megvalósító függvény. A paraméterként átadott rovar az
     * eredeti rovart tartalmazó tekronra klónozza
     */
    @Override
    public void applyTo(Insect original) {
        EntityRegistry registry = new EntityRegistry();
        Logger logger = new Logger(registry);

        List<Spore> prevEffects = original.getEffects();

        // Hatás alkalmazása a rovar instance-ra (bekerül az effects listájába)
        original.addEffects(this);

        // Copy konstruktort meghívjuk
        Insect clone = new Insect(original);
        Tecton currTecton = original.getTecton();
        // Hozzáadom a klónt az eredeti tektonjához
        currTecton.addInsect(clone);
        clone.getEntomologist().addInsects(clone);

        if (original.getEffects().contains(this)) {
            logger.logChange("INSECT", original, "EFFECT", prevEffects, original.getEffects());
        } else {
            logger.logError("EFFECT", "ACCELERATING EFFECT", "Nem sikerült alkalmazni a rovarra!");
        }

        if (currTecton.getInsects().contains(clone) && original.getEntomologist().getInsects().contains(clone)) {
            logger.logOk("INSECT", "NEW INSECT", "", "null", "created");
        } else {
            logger.logError("INSECT", "NEW INSECT", "Nem sikerült a klónt hozzáadni a tektonhoz vagy a rovarászhoz!");
        }
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = super.serialize(namer);

        obj.addProperty("sporeType", sporeType());

        return obj;
    }

} // end of cloneEffect
