package com.dino.engine;

import java.util.ArrayList;
import java.util.List;

import com.dino.core.Hexagon;
import com.dino.core.Hypha;
import com.dino.tecton.InfiniteHyphaTecton;
import com.dino.tecton.KeepHyphaTecton;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.ShortHyphaTecton;
import com.dino.tecton.SingleHyphaTecton;
import com.dino.tecton.Tecton;

/**
 * A játékteret kezelő és megvalósító osztály.
 * Eltárolja az azt alkotó összes tektont, amiken keresztül el tudja érni azok szomszédjait.
 */
public class GameBoard {
    /**
     * A játéktér összes tektonját tároló lista.
     */
    private List<Tecton> tectons;

    /**
     * Paraméter nélkül hívható függvény, ami legenerálja a játékteret.
     * Létrehozza a hexagonokat, kitöröl párat véletlenszerűen, a maradékot pedig tektonokká köti össze,
     * a végeredményt pedig felveszi a tectons listába.
     */
    public void generateBoard() {
    // Inicializáljuk a tektonok listáját
    tectons = new ArrayList<>();
    
    // 1. Lépés: HexagonGrid létrehozása "(10x10)"
    List<Hexagon> allHexagons = createHexagonGrid(10, 10);
    
    // 2. Lépés: Véletlenszerű hexagonok törlése
    removeRandomHexagons(allHexagons, 10); // "10%"" kitörlése
    
    // 3. Lépés: Tektonok létrehozása (1-4 Hexagonból)
    createTectons(allHexagons);
    
    // 4. Lépés: Szomszédsági viszonyok beállítása
    setTectonNeighbours();
}

    /**
     * Listaként visszaadja a paraméterként kapott tektonnal szomszédos összes másik tektont.
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
        
        // Minden tektonra megpróbáljuk elvégezni a törést
        for (Tecton tecton : tectons) {
            List<Tecton> splitResult = tecton.split(tecton.breakChance);
            
            // Ha a split művelet új tektonokat eredményezett
            if (!splitResult.isEmpty()) {
                newTectons.addAll(splitResult);
                tectonskToRemove.add(tecton);
                
                // Szomszédsági kapcsolatok beállítása
                if (splitResult.size() >= 2) {
                    Tecton.connectTectons(splitResult.get(0), splitResult.get(1));
                }
                
                // Szomszédsági kapcsolatok átvitele az eredeti tektonról
                for (Tecton neighbour : tecton.getNeighbours()) {
                    for (Tecton newTecton : splitResult) {
                        if (areTectonsAdjacent(newTecton, neighbour)) {
                            Tecton.connectTectons(newTecton, neighbour);
                        }
                    }
                }
            }
        }
        
        // Eltávolítjuk a kettétört tektonokat
        tectons.removeAll(tectonskToRemove);
        
        // Hozzáadjuk az új tektonokat
        tectons.addAll(newTectons);
    }

    /**
     * Összeköti a két Tectont szomszédságként, majd létrehoz és rögzít egy Hypha-t is.
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
        
        // Létrehozzuk a Hexagonokat egyedi azonosítókkal
        int id = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                hexagons.add(new Hexagon(id++));
            }
        }
        
        // Beállítjuk a szomszédságokat
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                List<Hexagon> neighbours = new ArrayList<>();
                int index = y * width + x;
                
                // A HexagonGrid szomszédságai
                // Páros és páratlan sorok eltérő kezelése
                boolean evenRow = y % 2 == 0;
                
                // Jobb szomszéd
                if (x < width - 1)
                    neighbours.add(hexagons.get(index + 1));
                
                // Bal szomszéd
                if (x > 0)
                    neighbours.add(hexagons.get(index - 1));
                
                // Felső sor szomszédok
                if (y > 0) {
                    // Felső középső
                    neighbours.add(hexagons.get(index - width));
                    
                    // Felső átlós (sorparitástól függően más)
                    if (evenRow && x > 0) {
                        neighbours.add(hexagons.get(index - width - 1)); // Bal felső átlós
                    } else if (!evenRow && x < width - 1) {
                        neighbours.add(hexagons.get(index - width + 1)); // Jobb felső átlós
                    }
                }
                
                // Alsó sor szomszédok
                if (y < height - 1) {
                    // Alsó középső
                    neighbours.add(hexagons.get(index + width));
                    
                    // Alsó átlós (sorparitástól függően más)
                    if (evenRow && x > 0) {
                        neighbours.add(hexagons.get(index + width - 1)); // Bal alsó átlós
                    } else if (!evenRow && x < width - 1) {
                        neighbours.add(hexagons.get(index + width + 1)); // Jobb alsó átlós
                    }
                }
                
                hexagons.get(index).setNeighbours(neighbours);
            }
        }
        
        return hexagons;
    }

    /**
     * Véletlenszerűen eltávolít hatszögeket a rácsból
     */
    private void removeRandomHexagons(List<Hexagon> hexagons, int percentToRemove) {
        int numToRemove = hexagons.size() * percentToRemove / 100;
        List<Hexagon> toRemove = new ArrayList<>();
        
        for (int i = 0; i < numToRemove; i++) {
            if (hexagons.isEmpty()) break;
            
            int indexToRemove = (int)(Math.random() * hexagons.size());
            Hexagon hexToRemove = hexagons.get(indexToRemove);
            
            hexToRemove.destroy();
            toRemove.add(hexToRemove);
        }
        
        hexagons.removeAll(toRemove);
    }

    /**
     * Létrehozza a tektonokat a hexagonokból
     */
    private void createTectons(List<Hexagon> hexagons) {
        List<Hexagon> remainingHexagons = new ArrayList<>(hexagons);
    
        while (!remainingHexagons.isEmpty()) {
            // Kiválasztunk egy kezdő hatszöget
            int startIndex = (int)(Math.random() * remainingHexagons.size());
            Hexagon startHex = remainingHexagons.get(startIndex);
            remainingHexagons.remove(startIndex);
            
            // Véletlenszerű tekton típus létrehozása
            Tecton newTecton = createRandomTectonType();
            
            // Hatszögek gyűjtése a tektonhoz (1-4 darab)
            List<Hexagon> tectonHexagons = new ArrayList<>();
            tectonHexagons.add(startHex);
            
            // Véletlenszerű méret 1 és "4" között
            int targetSize = 1 + (int)(Math.random() * 4);
            
            // Tekton növelése további szomszédokkal
            for (int i = 1; i < targetSize && !remainingHexagons.isEmpty(); i++) {
                // Az aktuális tekton hatszögeinek szomszédai közül választunk
                List<Hexagon> possibleNeighbours = new ArrayList<>();
                
                for (Hexagon hex : tectonHexagons) {
                    for (Hexagon neighbour : hex.getNeighbours()) {
                        if (remainingHexagons.contains(neighbour) && !possibleNeighbours.contains(neighbour)) {
                            possibleNeighbours.add(neighbour);
                        }
                    }
                }
                
                if (possibleNeighbours.isEmpty()) break;
                
                // Véletlenszerűen kiválasztunk egy szomszédot
                int nextIndex = (int)(Math.random() * possibleNeighbours.size());
                Hexagon nextHex = possibleNeighbours.get(nextIndex);
                
                tectonHexagons.add(nextHex);
                remainingHexagons.remove(nextHex);
            }
            
            // Beállítjuk a tekton hexagonjait
            newTecton.hexagons = tectonHexagons;
            
            // Hozzáadjuk a tektonok listájához
            tectons.add(newTecton);
        }
    }

    /**
     * Létrehoz egy véletlenszerű típusú tektont
     */
    private Tecton createRandomTectonType() {
        int type = (int)(Math.random() * 5);
        switch (type) {
            case 0: return new SingleHyphaTecton();
            case 1: return new NoFungiTecton();
            case 2: return new ShortHyphaTecton();
            case 3: return new KeepHyphaTecton();
            default: return new InfiniteHyphaTecton();
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
                if (areTectonsAdjacent(tectonA, tectonB)) {
                    // Kétirányú szomszédság beállítása a statikus metódussal
                    Tecton.connectTectons(tectonA, tectonB);
                }
            }
        }
    }

    /**
     * Ellenőrzi, hogy két tekton szomszédos-e
     */
    private boolean areTectonsAdjacent(Tecton a, Tecton b) {
        for (Hexagon hexA : a.hexagons) {
            for (Hexagon hexB : b.hexagons) {
                if (hexA.getNeighbours().contains(hexB)) {
                    return true;
                }
            }
        }
        return false;
    }


}

