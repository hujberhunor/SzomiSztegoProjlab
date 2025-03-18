<<<<<<< HEAD
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

    public void addSpores(Mycologist m) { }
    public void removeSpores(Mycologist m) { }
    public void addHypha(Hypha h) { }
    public List<Tecton> split(double breakChance) { return new ArrayList<>(); }
    public abstract void handleHypha(Hypha h);
    public void update() { }
}

=======
package com.dino;

import java.util.List;
import java.util.Map;

public class Tecton {
    private int hyphaLifespan;
    private int hyphaLimit;
    private boolean fungiEnabled;
    private double breakChance;
    private int breakCount;
    private Fungus fungus;
    private Entomologist insect;
    private List<Hexagon> hexagons;
    private Map<Mycologist, Integer> spores;
    private List<Hypha> hyphas;
}
>>>>>>> origin/NemTudomMajdÁtnevezem
