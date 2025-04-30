package com.dino.tecton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dino.core.Fungus;
import com.dino.core.Hexagon;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.engine.Game;
import com.dino.player.Mycologist;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.SerializableEntity;
import com.dino.util.SerializerUtil;
import com.google.gson.JsonObject;

public abstract class Tecton implements SerializableEntity {

    protected boolean fungiEnabled;
    protected int hyphaLimit;
    protected int hyphaLifespan;
    public double breakChance;
    public int breakCount;
    public List<Hexagon> hexagons;
    public List<Tecton> neighbours;
    public Fungus fungus;
    public List<Insect> insects;
    public Map<Spore, Integer> spores;
    public List<Hypha> hyphas;

    protected Tecton() {
        this.fungiEnabled = true;
        this.hyphaLimit = 0;
        this.hyphaLifespan = -1;
        this.breakChance = 5.0 + Math.random() * 35.0;
        this.breakCount = 0;
        this.hexagons = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.fungus = null;
        this.insects = new ArrayList<>();
        this.spores = new HashMap<>();
        this.hyphas = new ArrayList<>();
    }

    public List<Hypha> getHyphas() {
        return this.hyphas;
    }

    public void setHyphas(List<Hypha> hyphas) {
        this.hyphas = hyphas;
    }

    public List<Tecton> getNeighbours() {
        return neighbours;
    }

    private static final EntityRegistry registry = EntityRegistry.getInstance();
    private static final ObjectNamer namer = ObjectNamer.getInstance();
    private static final Logger logger = Logger.getInstance();

    public abstract Tecton createCopy();

    public void addSpores(Spore s){
        spores.put(s, spores.getOrDefault(s, 0) + 1);
    }

    public void addHypha(Hypha h) {
        hyphas.add(h);
    }

    public static void connectTectons(Tecton a, Tecton b) {
        if (a == null || b == null) {
            System.out.println("[ERROR] TECTON connectTectons Null Tecton connection");
            return;
        }

        String aName = namer.getName(a);
        String bName = namer.getName(b);

        if (!a.getNeighbours().contains(b)) {
            a.getNeighbours().add(b);
            System.out.println("[OK] TECTON " + aName + " NEIGHBOURS_ADD: - -> " + bName);
        }

        if (!b.getNeighbours().contains(a)) {
            b.getNeighbours().add(a);
            System.out.println("[OK] TECTON " + bName + " NEIGHBOURS_ADD: - -> " + aName);
        }
    }

    public static Hypha connectWithHypha(Tecton... tectons) {
        Hypha hypha = new Hypha();
        for (Tecton t : tectons) {
            hypha.getTectons().add(t);
            t.getHyphas().add(hypha);
        }
        return hypha;
    }

    public List<Tecton> split(double breakChance) {
        List<Tecton> resultTectons = new ArrayList<>();
        String currentTectonName = namer.getName(this);

        if (!insects.isEmpty()) {
            System.out.println("[ERROR] TECTON " + currentTectonName + " Nem törhet el: van rajta rovar");
            return resultTectons;
        }

        if (hexagons.size() <= 1) {
            System.out.println("[ERROR] TECTON " + currentTectonName + " Nem törhet el: csak egy hexagonból áll");
            return resultTectons;
        }

        if (breakCount >= 2) {
            System.out.println("[ERROR] TECTON " + currentTectonName + " Nem törhet el: már kétszer tört");
            return resultTectons;
        }

        boolean shouldBreak = Math.random() * 100 < this.breakChance;
        if (!shouldBreak) {
            System.out.println("[ERROR] TECTON " + currentTectonName + " Nem törhet el: valószínűség nem teljesült");
            return resultTectons;
        }

        Tecton tecton1 = this.createCopy();
        Tecton tecton2 = this.createCopy();

        divideHexagons(tecton1, tecton2);
        namer.register(tecton1);
        namer.register(tecton2);

        String tecton1Name = namer.getName(tecton1);
        String tecton2Name = namer.getName(tecton2);

        double newBreakChance = this.breakChance;
        int newBreakCount = this.breakCount + 1;

        if (newBreakCount == 1) {
            newBreakChance = this.breakChance / 2;
        } else if (newBreakCount == 2) {
            newBreakChance = 0;
        }

        tecton1.breakChance = newBreakChance;
        tecton1.breakCount = newBreakCount;
        tecton2.breakChance = newBreakChance;
        tecton2.breakCount = newBreakCount;

        tecton1.hyphas = new ArrayList<>();
        tecton2.hyphas = new ArrayList<>();

        if (fungus != null) {
            Tecton targetTecton = (Math.random() < 0.5) ? tecton1 : tecton2;
            targetTecton.fungus = this.fungus;
            System.out.println("[OK] FUNGUS " + namer.getName(fungus) + " LOCATION: " + currentTectonName + " -> " + namer.getName(targetTecton));
        }

        divideSpores(tecton1, tecton2);

        System.out.println("[OK] TECTON " + currentTectonName + " SPLIT: - -> " + tecton1Name + ", " + tecton2Name);
        resultTectons.add(tecton1);
        resultTectons.add(tecton2);

        return resultTectons;
    }

    public abstract void handleHypha(Hypha h);

    public boolean isNeighbor(Tecton t) {
        return neighbours.contains(t);
    }

    public boolean hasHypha(Tecton t) {
        for (Hypha h : hyphas) {
            List<Tecton> path = h.getTectons();
            for (int i = 0; i < path.size() - 1; i++) {
                Tecton a = path.get(i);
                Tecton b = path.get(i + 1);
                if ((a == this && b == t) || (a == t && b == this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasSpores(Mycologist m) {
        for (Spore spore : spores.keySet()) {
            if (spore.getSpecies().equals(m) && spores.get(spore) > 0) {
                return true;
            }
        }
        return false;
    }

    public Fungus getFungus() {
        return fungus;
    }

    public void setFungus(Fungus f) {
        this.fungus = f;
    }

    public void setNeighbours(List<Tecton> t) {
        this.neighbours = t;
    }

    public List<Insect> getInsects() {
        return insects;
    }

    public void addInsect(Insect insect) {
        insects.add(insect);
    }

    public double getBreakChance() {
        return breakChance;
    }

    private void divideHexagons(Tecton tecton1, Tecton tecton2) {
        if (hexagons.size() <= 1) return;
        int firstTectonHexCount = Math.max(1, hexagons.size() / 2);
        tecton1.hexagons = new ArrayList<>(hexagons.subList(0, firstTectonHexCount));
        tecton2.hexagons = new ArrayList<>(hexagons.subList(firstTectonHexCount, hexagons.size()));
    }

    private void divideSpores(Tecton tecton1, Tecton tecton2) {
        tecton1.spores = new HashMap<>();
        tecton2.spores = new HashMap<>();

        for (Map.Entry<Spore, Integer> entry : spores.entrySet()) {
            if (entry.getValue() <= 0) continue;
            if (Math.random() < 0.5) {
                tecton1.spores.put(entry.getKey(), entry.getValue());
            } else {
                tecton2.spores.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public List<Hexagon> getHexagons() {
        return hexagons;
    }

    public Map<Spore, Integer> getSporeMap() {
        return spores;
    }

    public void hyphaDecay(Game game) {
        for (Hypha h : hyphas) {
            if (h.getLifespan() <= 0) {
                int index = -1;
                for (int i = 0; i < h.getTectons().size(); i++) {
                    if (h.getTectons().get(i).equals(this)) {
                        index = i;
                        break;
                    }
                }
                if (index >= 0) {
                    Hypha newHypha = new Hypha(h.getMycologist(), h.getFungus());
                    namer.register(newHypha);
                    for (Tecton t : h.getTectons().subList(index, h.getTectons().size())) {
                        newHypha.getTectons().add(t);
                    }
                    h.getTectons().subList(index, h.getTectons().size()).clear();
                    game.addDecayedHypha(newHypha);
                }
            }
        }
    }

    @Override
    public JsonObject serialize(ObjectNamer namer) {
        JsonObject obj = new JsonObject();

        if (hexagons.contains(null)) {
            throw new RuntimeException("Tecton hexagons list contains NULL!");
        }
        if (neighbours.contains(null)) {
            throw new RuntimeException("Tecton neighbours list contains NULL!");
        }

        obj.addProperty("name", namer.getName(this));
        obj.addProperty("type", this.getClass().getSimpleName());

        obj.add("hexagons", SerializerUtil.toJsonArray(hexagons, h -> Integer.toString(h.getId())));
        obj.add("neighbours", SerializerUtil.toJsonArray(neighbours, namer::getName));
        obj.addProperty("breakChance", breakChance);
        obj.addProperty("breakCount", breakCount);

        if (fungus != null) {
            obj.addProperty("fungus", namer.getName(fungus));
        }

        obj.add("insects", SerializerUtil.toJsonArray(insects, namer::getName));
        obj.add("hyphas", SerializerUtil.toJsonArray(hyphas, namer::getName));

        JsonObject sporesObj = new JsonObject();
        for (Map.Entry<Spore, Integer> entry : spores.entrySet()) {
            sporesObj.addProperty(namer.getName(entry.getKey()), entry.getValue());
        }
        obj.add("spores", sporesObj);

        return obj;
    }

    public static Tecton deserialize(JsonObject obj, ObjectNamer namer) {
        EntityRegistry registry = EntityRegistry.getInstance();
        String type = obj.get("type").getAsString();
        Tecton tecton;
        switch (type) {
            case "SingleHyphaTecton": tecton = new SingleHyphaTecton(); break;
            case "InfiniteHyphaTecton": tecton = new InfiniteHyphaTecton(); break;
            case "ShortHyphaTecton": tecton = new ShortHyphaTecton(); break;
            case "KeepHyphaTecton": tecton = new KeepHyphaTecton(); break;
            case "NoFungiTecton": tecton = new NoFungiTecton(); break;
            default: throw new IllegalArgumentException("Ismeretlen Tecton típus: " + type);
        }

        namer.register(obj.get("name").getAsString(), tecton);

        tecton.hexagons = new ArrayList<>();
        for (var hexElem : obj.getAsJsonArray("hexagons")) {
            int hexId = Integer.parseInt(hexElem.getAsString());
            tecton.hexagons.add((Hexagon) registry.getByName("hex_" + hexId));
        }

        tecton.neighbours = new ArrayList<>();
        for (var neighbourElem : obj.getAsJsonArray("neighbours")) {
            tecton.neighbours.add((Tecton) registry.getByName(neighbourElem.getAsString()));
        }

        tecton.breakChance = obj.get("breakChance").getAsDouble();
        tecton.breakCount = obj.get("breakCount").getAsInt();

        if (obj.has("fungus")) {
            tecton.fungus = (Fungus) registry.getByName(obj.get("fungus").getAsString());
        }

        tecton.insects = new ArrayList<>();
        if (obj.has("insects")) {
            for (var insectElem : obj.getAsJsonArray("insects")) {
                tecton.insects.add((Insect) registry.getByName(insectElem.getAsString()));
            }
        }

        tecton.hyphas = new ArrayList<>();
        if (obj.has("hyphas")) {
            for (var hyphaElem : obj.getAsJsonArray("hyphas")) {
                tecton.hyphas.add((Hypha) registry.getByName(hyphaElem.getAsString()));
            }
        }

        tecton.spores = new HashMap<>();
        if (obj.has("spores")) {
            JsonObject sporesObj = obj.getAsJsonObject("spores");
            for (var entry : sporesObj.entrySet()) {
                Spore spore = (Spore) registry.getByName(entry.getKey());
                tecton.spores.put(spore, entry.getValue().getAsInt());
            }
        }

        return tecton;
    }

    @Override
    public String toString() {
        String name = namer.getName(this);
        if (name != null) return name;

        if (hexagons != null && !hexagons.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName()).append("(");
            for (int i = 0; i < hexagons.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(hexagons.get(i).toString());
            }
            sb.append(")");
            return sb.toString();
        }

        return this.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this));
    }
}

