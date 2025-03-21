package com.dino;

import java.util.*;

public abstract class Tecton {
    protected int hyphaLifespan;
    protected int hyphaLimit;
    protected boolean fungiEnabled;
    protected double breakChance;
    protected int breakCount;
    protected Fungus fungus;
    protected Insect insect;
    protected List<Hexagon> hexagons;
    protected Map<Mycologist, Integer> spores;
    protected List<Hypha> hyphas;

    public Tecton(){}
    public void addSpores(Mycologist m) { }
    public void removeSpores(Mycologist m) { }
    public void addHypha(Hypha h) { }
    public List<Tecton> split(double breakChance) { return new ArrayList<>(); }
    public abstract void handleHypha(Hypha h);
    public void update() { }
}
