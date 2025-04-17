package com.dino.tecton;

import com.dino.core.Hypha;

public class KeepHyphaTecton extends Tecton {
    private int hyphaLifespan; 

    public KeepHyphaTecton() {
        super();
        this.hyphaLifespan = -1; /// Tehát örökké él
    }

    @Override
    public void handleHypha(Hypha h) {
        //TODO
    }
    @Override
    public Tecton createCopy() {
        return new KeepHyphaTecton();
    }
}
