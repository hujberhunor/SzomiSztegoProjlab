package com.dino.view;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.tecton.Tecton;
import javafx.scene.Node;

import java.util.List;

public class TectonEntity extends Entity {
    protected Tecton type;
    protected Fungus fungus;
    protected List<Hypha> hyphas;
    protected List<Insect> insects;

    public TectonEntity(Tecton t) {
        type = t;
        fungus = t.getFungus();
        hyphas = t.getHyphas();
        insects = t.getInsects();
    }

    /**
     * A tecton kirajzolása
     * @return A kirajzolt tectont tartalmazó Node objektum
     */
    @Override
    public Node draw() {
        return null;
    }
}