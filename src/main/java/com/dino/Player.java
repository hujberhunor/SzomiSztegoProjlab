package com.dino;

public class Player {
    private String name;
    private int score;
    private int actionsPerTurn;
    private int remainingActions;

    public Tecton selectTecton(Tecton tecton) {
        return tecton;
    }

    public int calculateScore() {
        return score;
    }
}
