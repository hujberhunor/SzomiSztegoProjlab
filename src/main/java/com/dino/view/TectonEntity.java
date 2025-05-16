package com.dino.view;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.tecton.Tecton;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Map;

public class TectonEntity extends Entity {
    protected Tecton type;
    protected Fungus fungus;
    protected List<Hypha> hyphas;
    protected List<Insect> insects;
    protected Map<Spore, Integer> spores; // Spórák tárolása

    public TectonEntity(Tecton t) {
        type = t;
        fungus = t.getFungus();
        hyphas = t.getHyphas();
        insects = t.getInsects();
        spores = t.spores; // Spórák átvétele a tectonból
    }

    /**
     * Ellenőrzi, hogy a tectonon van-e spóra
     * 
     * @return Igaz, ha van legalább egy spóra a tectonon
     */
    public boolean hasSpores() {
        return spores != null && !spores.isEmpty();
    }

    /**
     * Visszaadja a spórák számát a tectonon
     * 
     * @return A spórák összesített száma
     */
    public int getSporeCount() {
        if (spores == null)
            return 0;

        int count = 0;
        for (Integer value : spores.values()) {
            count += value;
        }
        return count;
    }

    /**
     * A tecton kirajzolása
     * 
     * @return A kirajzolt tectont tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null;
    }
}