import java.util.*;

public class Insect {
    private Entomologist entomologist;
    private Tecton tecton;
    private List<Spore> effects;

    public boolean move(Tecton t) { return true; }
    public boolean cutHypha(Hypha h) { return true; }
    public void consumeSpores(Entomologist e) { }
    public void addEffects(Spore s) { }
    public void removeExpiredEffects() { }
}

