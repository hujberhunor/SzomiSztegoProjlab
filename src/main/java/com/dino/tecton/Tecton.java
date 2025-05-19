package com.dino.tecton;

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
import com.dino.util.Skeleton;
import com.google.gson.JsonObject;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ennek az absztrakt osztálynak a leszármazottjai reprezentálják a játékteret
 * alkotó egységeket, vagy mezőket.
 * Tárolja a mezőket alkotó kisebb hatszögeket, illetve az egész egységre
 * vonatkozó tulajdonságokat és annak állapotát.
 * A tekton altípusoknak az ősosztálya.
 */
public abstract class Tecton implements SerializableEntity {

    // Attribútumok
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

    private Color color;

    /**
     * Alapértelmezett konstruktor
     */
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

    // private static final ObjectNamer namer = ObjectNamer.getInstance(new
    // EntityRegistry());

    private static final EntityRegistry registry = EntityRegistry.getInstance();
    private static final ObjectNamer namer = ObjectNamer.getInstance();
    private static final Logger logger = Logger.getInstance();

    /**
     * Létrehoz egy ugyanolyan típusú új tektont.
     * Minden alosztály felülírja ezt a metódust.
     *
     * @return Új, az aktuálissal megegyező típusú tekton
     */
    public abstract Tecton createCopy();

    /**
     * A tektonon elhelyez egy darab, m gombász gombájából származó spórát.
     *
     * @param m A gombász, akinek a gombájából a spóra származik
     */
    public void addSpores(Spore s) {
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Tecton", "add spores");

        spores.put(s, spores.getOrDefault(s, 0) + 1);
        // skeleton.log("Spóra elhelyezve");
        // skeleton.log("A tektonon található gombász-spóraszám párok:");
        // spores.forEach((key, value) -> skeleton.log(key + " = " + value));
    }

    /**
     * A tektonról eltávolít egy darab, m gombász gombájából származó spórát,
     * amennyiben lehetséges.
     *
     * @param m A gombász, akinek a gombájából a spóra származik
     */
    public void removeSpores(Spore s) {
        // Skeleton skeleton = Skeleton.getInstance();
        // skeleton.startMethod("Tecton", "remove spores");

        spores.computeIfPresent(s, (key, value) -> (value > 1) ? value - 1 : null);
        // skeleton.log("Spóra eltávolítva");
        if (!spores.isEmpty()) {
            // skeleton.log("A tektonon található gombász-spóraszám párok:");
            // spores.forEach((key, value) -> skeleton.log(key + " = " + value));
        } else {
            // skeleton.log("A tektonon nem található spóra");
        }
    }

    /**
     * A tektonon elhelyezi a paraméterként kapott gombafonalat.
     * A Hypha osztály tárolja, hogy melyik gombász gombájától származik.
     *
     * @param h Az elhelyezendő gombafonál
     */
    public void addHypha(Hypha h) {
        hyphas.add(h);
    }

    /**
     * Kétirányú asszociáció miatt delegáltam fv-be
     */
    public static void connectTectons(Tecton a, Tecton b) {
        if (a == null || b == null) {
            System.out.println(
                    "[ERROR] TECTON connectTectons Null Tecton connection");
            return;
        }

        String aName = namer.getName(a);
        String bName = namer.getName(b);

        if (!a.getNeighbours().contains(b)) {
            a.getNeighbours().add(b);
            System.out.println(
                    "[OK] TECTON " + aName + " NEIGHBOURS_ADD: - -> " + bName);
        }

        if (!b.getNeighbours().contains(a)) {
            b.getNeighbours().add(a);
            System.out.println(
                    "[OK] TECTON " + bName + " NEIGHBOURS_ADD: - -> " + aName);
        }
    }

    /**
     * Kétirányú asszociáció miatt kell
     * Nagyon random de elv működik.
     */
    public static Hypha connectWithHypha(Tecton... tectons) {
        Hypha hypha = new Hypha();
        for (Tecton t : tectons) {
            hypha.getTectons().add(t);
            t.getHyphas().add(hypha);
        }
        return hypha;
    }

    /**
     * Minden kör után hívódó függvény, ami breakChance eséllyel, hatszögek mentén
     * létrehoz két új tektont
     * a saját helyén, míg magát megszűnteti. Az új tektonok tulajdonságai egyeznek
     * az őket létrehozó tulajdonságaival,
     * amiről a gombatest és rovarok véletlenszerűen választott töredékekre kerülnek
     * át, a gombafonalak pedig megsemmisülnek.
     * Visszaadja a két új tekton listaként.
     *
     * @param breakChance Törési esély
     * @param namer       Az ObjectNamer példány, amit a névkezeléshez használunk
     * @return Az újonnan létrehozott két tekton listája
     */
    public List<Tecton> split(double breakChance) {
        List<Tecton> resultTectons = new ArrayList<>();

        String currentTectonName = namer.getName(this);

        // Ellenőrizzük, hogy a tekton nem csak egy hexagonból áll-e
        if (hexagons.size() <= 1) {
            System.out.println("[ERROR] TECTON " + currentTectonName + " Nem törhet el: csak egy hexagonból áll");
            return resultTectons;
        }

        // Ha már kétszer tört a tekton, akkor nem törhet újra
        if (breakCount >= 2) {
            System.out.println("[ERROR] TECTON " + currentTectonName + " Nem törhet el: már kétszer tört");
            return resultTectons;
        }

        // Ha van rajta rovar, nem törhet el a tekton
        if (!insects.isEmpty()) {
            System.out.println("[ERROR] TECTON " + currentTectonName + " Nem törhet el: van rajta rovar");
            return resultTectons;
        }

        boolean shouldBreak;

        if (breakChance < 0) { // A paraméterként kapott breakChance értéket ellenőrizzük
            // Ha negatív értéket kap, akkor garantáltan törni fog
            shouldBreak = true;
        } else {
            // Normál működés: véletlenszám generálás
            shouldBreak = Math.random() * 100 < this.breakChance;
        }

        if (!shouldBreak) {
            System.out.println(
                    "[ERROR] TECTON " +
                            currentTectonName +
                            " Nem törhet el: valószínűség nem teljesült");
            return resultTectons;
        }

        // A fonalakat elszakítjuk
        for (Hypha h : hyphas) {
            List<Tecton> tectons = h.getTectons();
            if (!h.containsInfiniteTecton()) {
                for (int i = 0; i < tectons.size(); i++) {
                    if (tectons.get(i).equals(this)) {
                        tectons.subList(i, tectons.size()).clear();
                        break;
                    }
                }
            }
        }

        // Két új tekton létrehozása
        Tecton tecton1 = this.createCopy();
        Tecton tecton2 = this.createCopy();

        // Hexagonok felosztása és egyéb műveletek
        divideHexagons(tecton1, tecton2);

        // FONTOS: csak a hexagonok beállítása után regisztrálunk!
        namer.register(tecton1);
        namer.register(tecton2);

        // ÚJ: A két új Tecton közötti szomszédsági kapcsolat beállítása
        Tecton.connectTectons(tecton1, tecton2);

        // ÚJ: Szomszédsági kapcsolatok átvitele az eredeti Tectonról
        for (Tecton neighbour : this.neighbours) {
            if (!neighbour.equals(this)) { // Ne adjuk hozzá saját magát
                if (areTectonsNeighbours(tecton1, neighbour)) {
                    Tecton.connectTectons(tecton1, neighbour);
                }
                if (areTectonsNeighbours(tecton2, neighbour)) {
                    Tecton.connectTectons(tecton2, neighbour);
                }
            }
        }

        // Törési valószínűségek és számláló frissítése
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

        // Gombatestek véletlenszerű áthelyezése
        if (fungus != null) {
            Tecton targetTecton = (Math.random() < 0.5) ? tecton1 : tecton2;
            targetTecton.fungus = this.fungus;
            System.out.println(
                    "[OK] FUNGUS " +
                            namer.getName(fungus) +
                            " LOCATION: " +
                            currentTectonName +
                            " -> " +
                            namer.getName(targetTecton));
        }

        // Spórák elosztása
        divideSpores(tecton1, tecton2);

        // Logoljuk a sikeres törést
        System.out.println(
                "[OK] TECTON " +
                        currentTectonName +
                        " SPLIT: - -> " +
                        namer.getName(tecton1) +
                        ", " +
                        namer.getName(tecton2));

        resultTectons.add(tecton1);
        resultTectons.add(tecton2);

        return resultTectons;
    }

    // Új segédmetódus a Tecton osztályban
    private boolean areTectonsNeighbours(Tecton a, Tecton b) {
        for (Hexagon hexA : a.hexagons) {
            for (Hexagon hexB : b.hexagons) {
                if (hexA.getNeighbours().contains(hexB)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * A fonalakat kezelő virtuális függvény, amelyet az alosztályok felülírnak.
     *
     * @param h A kezelendő gombafonál
     */
    public abstract void handleHypha(Hypha h);

    public boolean isNeighbor(Tecton t) {
        return neighbours.contains(t);
    }

    /**
     * Ellenőrzi, hogy az aktuális tekton és a paraméterként megadott tekton
     * között van-e gombafonál összeköttetés.
     * Elsősorban a rovar szempontjából fontos, a fonal vágás akciójához.
     *
     * @param t A tekton, amellyel az összeköttetést ellenőrizzük
     * @return Igaz, ha van gombafonál a két tekton között, egyébként hamis
     */
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

    /**
     * Ellenőrzi, hogy a tektonon van-e a megadott gombásztól származó spóra.
     *
     * @param m A gombász, akinek a spóráját keressük.
     * @return Igaz, ha van legalább egy spóra a gombásztól, egyébként hamis.
     */
    public boolean hasSpores(Mycologist m) {
        return spores.keySet().stream().anyMatch(spore -> spore.getSpecies().equals(m));
    }

    /**
     * Visszaadja a tektonon lévő gombatestet
     *
     * @return A tektonon lévő gombatest
     */
    public Fungus getFungus() {
        return fungus;
    }

    /**
     * Beállítja a tektonon lévő gombatestet.
     *
     * @param f Az új gombatest, amelyet elhelyezünk a tektonon.
     */
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

    /**
     * Szétosztja a hexagonokat a két új tekton között úgy, hogy összefüggő és
     * közel azonos méretű csoportokat alkossanak.
     *
     * @param tecton1 Az első új tekton
     * @param tecton2 A második új tekton
     */
    private void divideHexagons(Tecton tecton1, Tecton tecton2) {
        if (hexagons.size() <= 1)
            return;

        // Két csoport létrehozása
        List<Hexagon> group1 = new ArrayList<>();
        List<Hexagon> group2 = new ArrayList<>();

        // A már kiosztott hexagonok nyilvántartása
        Set<Hexagon> assigned = new HashSet<>();

        // Két kiindulási hexagon választása
        Hexagon start1 = hexagons.get(0);
        Hexagon start2 = hexagons.get(hexagons.size() - 1);

        group1.add(start1);
        group2.add(start2);
        assigned.add(start1);
        assigned.add(start2);

        // Felváltva növeljük a két csoportot
        boolean addToFirst = true;

        while (assigned.size() < hexagons.size()) {
            // Az aktív csoport és a következő csoport kiválasztása
            List<Hexagon> activeGroup = addToFirst ? group1 : group2;

            // Keresünk egy új, még nem kiosztott hexagont, ami szomszédos a csoport
            // valamelyik elemével
            Hexagon nextHex = null;

            for (Hexagon hex : activeGroup) {
                for (Hexagon neighbor : hex.getNeighbours()) {
                    if (hexagons.contains(neighbor) && !assigned.contains(neighbor)) {
                        nextHex = neighbor;
                        break;
                    }
                }
                if (nextHex != null)
                    break;
            }

            // Ha találtunk csatlakoztatható hexagont, hozzáadjuk a csoporthoz
            if (nextHex != null) {
                activeGroup.add(nextHex);
                assigned.add(nextHex);
            }

            // Váltás a másik csoportra
            // De csak akkor, ha találtunk új hexagont, vagy ha mindkét csoportnál nem
            // találnánk új hexagont
            if (nextHex != null
                    || !hasAdjacentUnassigned(group1, assigned) && !hasAdjacentUnassigned(group2, assigned)) {
                addToFirst = !addToFirst;
            }

            // Ha egyik csoport sem tud tovább növekedni, de még vannak kiosztandó
            // hexagonok,
            // akkor az egyik csoporthoz adjuk a maradékot
            if (!hasAdjacentUnassigned(group1, assigned) && !hasAdjacentUnassigned(group2, assigned) &&
                    assigned.size() < hexagons.size()) {

                for (Hexagon hex : hexagons) {
                    if (!assigned.contains(hex)) {
                        // A kisebb csoporthoz adjuk
                        if (group1.size() <= group2.size()) {
                            group1.add(hex);
                        } else {
                            group2.add(hex);
                        }
                        assigned.add(hex);
                        break;
                    }
                }
            }
        }

        // Az új csoportok beállítása a tectonokban
        tecton1.hexagons = group1;
        tecton2.hexagons = group2;
    }

    /**
     * Ellenőrzi, hogy van-e a csoporthoz csatlakoztatható hexagon
     */
    private boolean hasAdjacentUnassigned(List<Hexagon> group, Set<Hexagon> assigned) {
        for (Hexagon hex : group) {
            for (Hexagon neighbor : hex.getNeighbours()) {
                if (hexagons.contains(neighbor) && !assigned.contains(neighbor)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Szétosztja a spórákat a két új tekton között.
     * Minden gombász spórája véletlenszerűen kerül az egyik vagy másik tektonra.
     *
     * @param tecton1 Az első új tekton
     * @param tecton2 A második új tekton
     */
    private void divideSpores(Tecton tecton1, Tecton tecton2) {
        tecton1.spores = new HashMap<>();
        tecton2.spores = new HashMap<>();

        for (Map.Entry<Spore, Integer> entry : spores.entrySet()) {
            Spore spore = entry.getKey();
            Integer sporeCount = entry.getValue();

            if (sporeCount <= 0)
                continue;

            // Véletlenszerűen választunk a két tekton között
            if (Math.random() < 0.5) {
                tecton1.spores.put(spore, sporeCount);
            } else {
                tecton2.spores.put(spore, sporeCount);
            }
        }
    }

    // szerializálás miatt
    public List<Hexagon> getHexagons() {
        return hexagons;
    }

    public Map<Spore, Integer> getSporeMap() {
        return spores;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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

        obj.add(
                "hexagons",
                SerializerUtil.toJsonArray(hexagons, h -> Integer.toString(h.getId())));

        obj.add(
                "neighbours",
                SerializerUtil.toJsonArray(neighbours, namer::getName));

        obj.addProperty("breakChance", breakChance);

        return obj;
    }

    @Override
    public String toString() {
        String name = namer.getName(this);
        if (name != null) {
            return name;
        }

        // If no name is registered, try to create a meaningful representation
        if (hexagons != null && !hexagons.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.getClass().getSimpleName()).append("(");
            for (int i = 0; i < hexagons.size(); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append(hexagons.get(i).toString());
            }
            sb.append(")");
            return sb.toString();
        }

        // Fallback to default
        return (this.getClass().getSimpleName() +
                "@" +
                Integer.toHexString(System.identityHashCode(this)));
    }

}