package com.dino.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

import com.dino.core.Hexagon;
import com.dino.core.Hypha;
import com.dino.tecton.InfiniteHyphaTecton;
import com.dino.tecton.KeepHyphaTecton;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.ShortHyphaTecton;
import com.dino.tecton.SingleHyphaTecton;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.view.GuiBoard;
import com.dino.view.ModelObserver;

/**
 * A játékteret kezelő és megvalósító osztály.
 * Eltárolja az azt alkotó összes tektont, amiken keresztül el tudja érni azok
 * szomszédjait.
 */
public class GameBoard {
    /**
     * A játéktér összes tektonját tároló lista.
     */
    private List<Tecton> tectons;

    private final EntityRegistry registry = EntityRegistry.getInstance();
    private final ObjectNamer namer = ObjectNamer.getInstance();
    private final Logger logger = Logger.getInstance();

    // A játéktér mérete
    private final int GRID_WIDTH = 10;
    private final int GRID_HEIGHT = 10;
    // Egy térkép, ami a hexagon koordinátákat tárolja (könnyebb azonosításhoz)
    private Map<Hexagon, int[]> hexagonCoordinates = new HashMap<>();

    public GameBoard() {
        tectons = new ArrayList<>();
    }

    /**
     * Paraméter nélkül hívható függvény, ami legenerálja a játékteret.
     * Létrehozza a hexagonokat, kitöröl párat véletlenszerűen, a maradékot pedig
     * tektonokká köti össze, a végeredményt pedig felveszi a tectons listába.
     */
    public void generateBoard() {
        // Inicializáljuk a tektonok listáját
        tectons = new ArrayList<>();
        hexagonCoordinates.clear();

        // 1. Lépés: HexagonGrid létrehozása
        List<Hexagon> allHexagons = createHexagonGrid(GRID_WIDTH, GRID_HEIGHT);

        // 2. Lépés: Véletlenszerű hexagonok törlése
        removeRandomHexagons(allHexagons, 10); // 10%-ot kitörlünk

        // 3. Lépés: Tektonok létrehozása (1-4 Hexagonból)
        createTectons(allHexagons);

        // 4. Lépés: Szomszédsági viszonyok beállítása
        setTectonNeighbours();
    }

    /**
     * Listaként visszaadja a paraméterként kapott tektonnal szomszédos összes másik
     * tektont.
     * 
     * @param t Vizsgálandó tekton.
     * @return A vizsgált tekton összes szomszédja.
     */
    public List<Tecton> getNeighbors(Tecton t) {
        return t.getNeighbours();
    }

    /**
     * A körök végén a tektonok törését kezelő függvény.
     */
    public void breakHandler() {
        List<Tecton> newTectons = new ArrayList<>();
        List<Tecton> tectonskToRemove = new ArrayList<>();

        // Minden tectonra megpróbáljuk elvégezni a törést
        for (Tecton tecton : tectons) {
            List<Tecton> splitResult = tecton.split(tecton.breakChance);

            // Ha a split művelet új tectonokat eredményezett
            if (!splitResult.isEmpty()) {
                // Regisztráljuk az új tectonokat
                for (Tecton newTecton : splitResult) {
                    namer.register(newTecton);
                }

                newTectons.addAll(splitResult);
                tectonskToRemove.add(tecton);

                // Szomszédsági kapcsolatok beállítása
                if (splitResult.size() >= 2) {
                    Tecton.connectTectons(splitResult.get(0), splitResult.get(1));
                    logger.logChange("TECTON", splitResult.get(0), "NEIGHBOURS_ADD", "-",
                            namer.getName(splitResult.get(1)));

                    // Itt hívd meg a recolorTecton metódust a Game osztályon keresztül
                    Game game = Game.getInstance();
                    for (ModelObserver observer : game.getObservers()) {
                        if (observer instanceof GuiBoard) {
                            ((GuiBoard) observer).recolorTecton(splitResult.get(0), splitResult.get(1));
                            break;
                        }
                    }
                }

                // Szomszédsági kapcsolatok átvitele az eredeti tektonról
                for (Tecton neighbour : tecton.getNeighbours()) {
                    if (!tectonskToRemove.contains(neighbour)) {
                        for (Tecton newTecton : splitResult) {
                            if (areTectonsNeighbours(newTecton, neighbour)) {
                                Tecton.connectTectons(newTecton, neighbour);
                                logger.logChange("TECTON", newTecton, "NEIGHBOURS_ADD", "-",
                                        namer.getName(neighbour));
                            }
                        }
                    }
                }

                // Logoljuk a sikeres törést
                logger.logChange("GAMEBOARD", tecton, "BREAK", namer.getName(tecton),
                        splitResult.stream()
                                .map(namer::getName)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("-"));
            }
        }

        // Eltávolítjuk a kettétört tektonokat
        tectons.removeAll(tectonskToRemove);

        // Hozzáadjuk az új tektonokat
        tectons.addAll(newTectons);

        Game.getInstance().notifyObservers();
    }

    /**
     * Összeköti a két Tectont szomszédságként, majd létrehoz és rögzít egy Hypha-t
     * is.
     */
    public Hypha connect(Tecton a, Tecton b) {
        Tecton.connectTectons(a, b); // kétirányú szomszédság

        Hypha hypha = new Hypha();
        hypha.connectTectons(a, b);

        a.addHypha(hypha);
        b.addHypha(hypha);

        return hypha;
    }

    /**
     * Létrehoz egy Hexagonrácsot
     */
    private List<Hexagon> createHexagonGrid(int width, int height) {
        List<Hexagon> hexagons = new ArrayList<>();
        hexagonCoordinates.clear();

        // Létrehozzuk a Hexagonokat egyedi azonosítókkal
        int id = 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Hexagon hex = new Hexagon(id++);
                hexagons.add(hex);
                // Eltároljuk a koordinátákat (segít majd a szomszédosság meghatározásában)
                hexagonCoordinates.put(hex, new int[] { x, y });
            }
        }

        // Most beállítjuk a szomszédságokat, figyelembe véve a koordinátákat
        for (Hexagon hex : hexagons) {
            int[] coords = hexagonCoordinates.get(hex);
            int x = coords[0];
            int y = coords[1];
            boolean evenRow = y % 2 == 0;

            List<Hexagon> neighbours = new ArrayList<>();

            // Vízszintes szomszédok
            // Bal szomszéd
            if (x > 0) {
                addNeighbourByCoords(neighbours, hexagons, x - 1, y);
            }
            // Jobb szomszéd
            if (x < width - 1) {
                addNeighbourByCoords(neighbours, hexagons, x + 1, y);
            }

            // Átlós szomszédok FELSŐ SOR
            if (y > 0) {
                // Középső felső
                addNeighbourByCoords(neighbours, hexagons, x, y - 1);

                // Páratlan és páros sorok eltérően kapcsolódnak átlósan
                if (evenRow) {
                    // Páros sorokban a bal felső átlós
                    if (x > 0) {
                        addNeighbourByCoords(neighbours, hexagons, x - 1, y - 1);
                    }
                } else {
                    // Páratlan sorokban a jobb felső átlós
                    if (x < width - 1) {
                        addNeighbourByCoords(neighbours, hexagons, x + 1, y - 1);
                    }
                }
            }

            // Átlós szomszédok ALSÓ SOR
            if (y < height - 1) {
                // Középső alsó
                addNeighbourByCoords(neighbours, hexagons, x, y + 1);

                // Páratlan és páros sorok eltérően kapcsolódnak átlósan
                if (evenRow) {
                    // Páros sorokban a bal alsó átlós
                    if (x > 0) {
                        addNeighbourByCoords(neighbours, hexagons, x - 1, y + 1);
                    }
                } else {
                    // Páratlan sorokban a jobb alsó átlós
                    if (x < width - 1) {
                        addNeighbourByCoords(neighbours, hexagons, x + 1, y + 1);
                    }
                }
            }

            hex.setNeighbours(neighbours);
        }

        return hexagons;
    }

    /**
     * Segédmetódus a szomszéd hozzáadásához koordináták alapján
     */
    private void addNeighbourByCoords(List<Hexagon> neighbours, List<Hexagon> allHexagons, int x, int y) {
        // Ellenőrizzük, hogy a koordináták érvényesek-e
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return;
        }

        // Keressük meg a hexagont ezen a koordinátán
        for (Hexagon hex : allHexagons) {
            int[] coords = hexagonCoordinates.get(hex);
            if (coords != null && coords[0] == x && coords[1] == y) {
                neighbours.add(hex);
                return;
            }
        }
    }

    /**
     * Véletlenszerűen eltávolít hatszögeket a rácsból
     */
    private void removeRandomHexagons(List<Hexagon> hexagons, int percentToRemove) {
        int numToRemove = hexagons.size() * percentToRemove / 100;
        List<Hexagon> toRemove = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numToRemove; i++) {
            if (hexagons.isEmpty())
                break;

            int indexToRemove = random.nextInt(hexagons.size());
            Hexagon hexToRemove = hexagons.get(indexToRemove);

            hexToRemove.destroy(); // Törli a szomszédsági kapcsolatokat
            toRemove.add(hexToRemove);
            hexagonCoordinates.remove(hexToRemove); // Koordináták törlése
        }

        hexagons.removeAll(toRemove);
    }

    /**
     * Létrehozza a tektonokat a hexagonokból
     */
    private void createTectons(List<Hexagon> hexagons) {
        List<Hexagon> remainingHexagons = new ArrayList<>(hexagons);

        // Üres hexagonok szűrése
        remainingHexagons.removeIf(h -> h.getNeighbours().isEmpty());

        List<Tecton> singleHexTectons = new ArrayList<>(); // Itt gyűjtjük az 1 hexagonból álló tektonokat

        while (!remainingHexagons.isEmpty()) {
            // Kiválasztunk egy kezdő hatszöget
            int startIndex = (int) (Math.random() * remainingHexagons.size());
            Hexagon startHex = remainingHexagons.remove(startIndex);

            // Véletlenszerű tekton típus létrehozása
            Tecton newTecton = createRandomTectonType();

            // Hatszögek gyűjtése a tektonhoz (1-4 darab)
            List<Hexagon> tectonHexagons = new ArrayList<>();
            tectonHexagons.add(startHex);

            // Véletlenszerű méret 1 és 8 között
            int targetSize = 1 + (int) (Math.random() * 8);

            // Egybefüggő tekton kialakítása
            growTecton(tectonHexagons, remainingHexagons, targetSize);

            // Hexagonok beállítása a tectonban
            newTecton.hexagons = tectonHexagons;

            // Regisztrálás
            namer.register(newTecton);

            // Ha csak egy hexagonból áll, félretesszük későbbi feldolgozásra
            if (tectonHexagons.size() == 1) {
                singleHexTectons.add(newTecton);
            } else {
                // Különben hozzáadjuk a rendes tektonok listájához
                tectons.add(newTecton);
                logger.logOk("GAMEBOARD", "board", "CREATE_TECTON", "-", namer.getName(newTecton));
            }
        }

        // Második körben feldolgozzuk az 1 hexagononból álló tektonokat
        handleSingleHexagonTectons(singleHexTectons);
    }

    /**
     * Feldolgozza az egy hexagonból álló tektonokat, megpróbálja őket más tektonhoz
     * kapcsolni
     */
    private void handleSingleHexagonTectons(List<Tecton> singleHexTectons) {
        for (Tecton singleTecton : singleHexTectons) {
            boolean merged = false;

            // Ellenőrizzük, hogy van-e szomszédos tekton a játéktéren
            Hexagon singleHex = singleTecton.hexagons.get(0);

            // Sorrendben próbáljuk összevonni a legkisebb tektonnal
            List<Tecton> possibleMergeTectons = new ArrayList<>();

            for (Tecton existingTecton : tectons) {
                for (Hexagon existingHex : existingTecton.hexagons) {
                    if (singleHex.getNeighbours().contains(existingHex)) {
                        possibleMergeTectons.add(existingTecton);
                        break; // Elég egy kapcsolatot találni az adott tektonban
                    }
                }
            }

            // Ha van lehetséges összevonási jelölt, rendezzük őket méret szerint növekvő
            // sorrendbe
            if (!possibleMergeTectons.isEmpty()) {
                possibleMergeTectons.sort((t1, t2) -> Integer.compare(t1.hexagons.size(), t2.hexagons.size()));

                // Összevonjuk a legkisebb tektonnal
                Tecton targetTecton = possibleMergeTectons.get(0);
                targetTecton.hexagons.add(singleHex);
                logger.logOk("GAMEBOARD", "board", "MERGE_TECTON", namer.getName(singleTecton),
                        namer.getName(targetTecton));
                merged = true;
            }

            // Ha nem sikerült összevonni, akkor is hozzáadjuk a tektonok listájához
            if (!merged) {
                tectons.add(singleTecton);
                logger.logOk("GAMEBOARD", "board", "CREATE_TECTON", "-", namer.getName(singleTecton));
            }
        }
    }

    /**
     * Létrehoz egy véletlenszerű típusú tektont
     */
    private Tecton createRandomTectonType() {
        int type = (int) (Math.random() * 5);
        switch (type) {
            case 0:
                return new SingleHyphaTecton();
            case 1:
                return new NoFungiTecton();
            case 2:
                return new ShortHyphaTecton();
            case 3:
                return new KeepHyphaTecton();
            default:
                return new InfiniteHyphaTecton();
        }
    }

    /**
     * Beállítja a tektonok közötti szomszédsági viszonyokat
     */
    private void setTectonNeighbours() {
        for (int i = 0; i < tectons.size(); i++) {
            Tecton tectonA = tectons.get(i);

            for (int j = i + 1; j < tectons.size(); j++) {
                Tecton tectonB = tectons.get(j);

                // Ellenőrizzük, hogy a két tekton szomszédos-e
                if (areTectonsNeighbours(tectonA, tectonB)) {
                    // Kétirányú szomszédság beállítása
                    Tecton.connectTectons(tectonA, tectonB);
                }
            }
        }
    }

    /**
     * Ellenőrzi, hogy két tekton szomszédos-e
     */
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
     * Növeszti a tektont egybefüggő formában a célméretig vagy amíg lehetséges
     * 
     * @param tectonHexagons    Az aktuális tektonhoz már hozzáadott hexagonok
     * @param remainingHexagons A még fel nem használt hexagonok
     * @param targetSize        A kívánt célméret
     */
    private void growTecton(List<Hexagon> tectonHexagons, List<Hexagon> remainingHexagons, int targetSize) {
        // Folytatjuk a hexagonok hozzáadását, amíg el nem érjük a célméretet vagy nincs
        // több szomszéd
        while (tectonHexagons.size() < targetSize) {
            // Összegyűjtjük az összes lehetséges szomszédot
            List<Hexagon> possibleNeighbors = new ArrayList<>();

            // Minden már hozzáadott hexagon szomszédját vizsgáljuk
            for (Hexagon hex : tectonHexagons) {
                for (Hexagon neighbor : hex.getNeighbours()) {
                    if (remainingHexagons.contains(neighbor) && !possibleNeighbors.contains(neighbor) &&
                            !tectonHexagons.contains(neighbor)) {
                        possibleNeighbors.add(neighbor);
                    }
                }
            }

            if (possibleNeighbors.isEmpty()) {
                // Nincs több lehetséges szomszéd, befejezzük a növekedést
                break;
            }

            // Véletlenszerűen kiválasztunk egy szomszédot
            int nextIndex = (int) (Math.random() * possibleNeighbors.size());
            Hexagon nextHex = possibleNeighbors.get(nextIndex);

            // Hozzáadjuk a kiválasztott hexagont a tektonhoz
            tectonHexagons.add(nextHex);
            remainingHexagons.remove(nextHex);
        }
    }

    // Getter a tectonokhoz (szerializálás miatt)
    public List<Tecton> getAllTectons() {
        return tectons;
    }

    public List<Tecton> getTectons() {
        return tectons;
    }
}