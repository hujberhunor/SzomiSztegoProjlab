package com.dino;

public class KeepHyphaTecton extends Tecton {
    private int hyphaLifespan; 

    public KeepHyphaTecton() {
        super();
        this.hyphaLifespan = -1; /// Tehát örökké él
    }

    @Override
    protected void handleHypha(Hypha h) {
        //TODO
    }
}
