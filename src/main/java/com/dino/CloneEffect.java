package com.dino;

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

    /*
     * A gomba hatását megvalósító függvény. A paraméterként átadott rovar az
     * eredeti rovart tartalmazó tekronra klónozza
     */
    @Override
    public void applyTo(Insect original) {
        Skeleton skeleton = Skeleton.getInstance();
        skeleton.startMethod("CloneEffect", "applyTo");

        // Hatás alkalmazása a rovar instance-ra (bekerül az effects listájába)
        original.addEffects(this);

        // Copy konstruktort meghívjuk
        Insect clone = new Insect(original);
        Tecton currTecton = original.getTecton();
        // Hozzáadaom a klónt az eredeti tektonjához
        currTecton.getInsects().add(clone);

        skeleton.log("CloneEffect hatás alkalmazva: Klón a tektonra helyezve.");
        skeleton.endMethod();
    }

} // end of cloneEffect
