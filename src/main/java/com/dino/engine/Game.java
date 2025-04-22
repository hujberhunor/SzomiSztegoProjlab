package com.dino.engine;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.dino.core.Hypha;
import com.dino.player.Player;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

/**
 * A játékmenet alapvető funkcióit vezérlő, és annak tulajdonságait tároló és kezelő osztály.
 */
public class Game {
    /**
     * A játékteret és annak tulajdonságait reprezentáló objektum.
     */
    private GameBoard map;

    /**
     * A játékosokat tartalmazó lista.
     */
    private List<Player> players;

    /**
     * Egy egész szám, ami azt tartja számon, hogy a játék menete alatt hány kör telt el.
     * Értéke eggyel nő, ha már minden játékos lépett.
     */
    private int currTurn;

    /**
     * Egy egész szám, ami eltárolja, hogy a játék hány kör után fog véget érni.
     * Értékét a felhasználók adják meg a meccs kezdete előtt.
     */
    private int totalTurns;

    /**
     * Az a játékos, aki éppen léphet.
     * Értéke a maximális mennyiségű akció felhasználása után a soron következő játékosra vált.
     */
    private Player currentPlayer;

    private final EntityRegistry registry;
    private final Logger logger;
    private final GameBoard board;

    // Jó
    /**
     * Nem élő fonalak listája
     */
    private List<Hypha> decayedHypha;

    private Object selectedEntity;

    public Game(int totalTurns) {
        this.map = new GameBoard();
        this.players = new ArrayList<Player>();
        this.currTurn = 0;
        this.totalTurns = totalTurns;
        this.currentPlayer = null;
        this.decayedHypha = new ArrayList<>();
        this.registry = new EntityRegistry();
        this.logger = new Logger(registry);
        this.board = new GameBoard();
    }

    /**
     * Paraméter nélkül hívható függvény, ami legenerálja a játékteret.
     */
    public void initBoard() {
        map.generateBoard();
    }

    /**
     * Paraméter nélkül hívható függvény, ami a játék elemeinek inicializálásáért felel.
     * A már legenerált játéktérben a felhasználótól kapott bemenetek szerint felveszi,
     * és elhelyezi a játékosokat a kezdeti tektonokon, és inicializálja a játékmenet kezdeti értékeit.
     * @return A játék inicializálásának sikeressége.
     */
    public boolean initGame() {
        if (players.isEmpty() || map == null) {
            return false;
        }

        for (Player player : players) {
            player.score = 0;
            player.remainingActions = player.actionsPerTurn;
        }

        if (!players.isEmpty()) {
            currentPlayer = players.get(0);
        }

        currTurn = 0;
        decayedHypha.clear();

        return true;
    }

    /**
     * A játék első körtől való indításáért felelő függvény.
     */
    public void startGame() {
        currTurn = 1;

        if (currentPlayer == null && !players.isEmpty()) {
            currentPlayer = players.get(0);
        }

        for (Player player : players) {
            player.remainingActions = player.actionsPerTurn;
        }
    }

    /**
     * Felveszi az új paraméterként kapott játékost, és visszaadja, hogy a művelet sikeres volt-e.
     * @param player Felvenni kívánt új játékos.
     * @return Az új játékos felvételének sikeresége.
     */
    public boolean addPlayer(Player player) {
        if (player == null || players.contains(player)) {
            return false;
        }

        if (players.size() == 1) {
            currentPlayer = player;
        }
        players.add(player);

        return true;
    }

    /**
     * Törli a paraméterként kapott játékost, és visszaadja, hogy a művelet sikeres volt-e.
     * @param player Töröli kívánt játékos.
     * @return Az játékos törlésének sikeressége.
     */
    public boolean removePlayer(Player player) {
        if (player == null || !players.contains(player)) {
            return false;
        }

        int playerIndex = players.indexOf(player);
        boolean result = players.remove(player);

        if (player == currentPlayer) {
            if (players.isEmpty()) {
                currentPlayer = null;
            } else {
                // A következő játékos lesz az aktuális, vagy az első, ha ez volt az utolsó
                int nextIndex = playerIndex % players.size();
                currentPlayer = players.get(nextIndex);
            }
        }

        return result;
    }

    /**
     * Paraméter nélkül hívható függvény, ami lépteti a játékmenetet egy lépéssel.
     */
    public void nextTurn() {
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();

        if (nextIndex == 0) {
            currTurn++;

            // Ha elértük a maximális köröket, a játék véget ér
            if (currTurn > totalTurns) {
                endGame();
                return;
            }
            map.breakHandler();
        }

        currentPlayer = players.get(nextIndex);
        currentPlayer.remainingActions = currentPlayer.actionsPerTurn;
    }

    /**
     * Paraméter nélkül hívható függvény, ami befejezi a játékot.
     * Ez a függvény választja ki a két győztest a számontartott pontszámok alapján,
     * és akkor hívódik, amikor a CurrTurn értéke eléri a TotalTurns plusz egy értéket.
     */
    public void endGame() {
        for (Player player : players) {
            player.score = player.calculateScore();
        }

        // Játékosok sorba rendezése pontszám szerint (csökkenő sorrendben)
        players.sort(Comparator.comparingInt((Player p) -> p.score).reversed());

        System.out.println("A játék véget ért!");

        System.out.println("Végső állás:");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            System.out.println((i + 1) + ". helyezett: " + player.name + " - Pontszám: " + player.score);
        }
    }

    /**
     * Visszaadja a jelenlegi játékost
     * @return Az aktuális játékos
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Visszaadja a játéktáblát
     * @return A játéktábla objektum
     */
    public GameBoard getMap() {
        return map;
    }

    /**
     * Visszaadja a játékosok listáját
     * @return A játékosok listája
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Visszaadja a jelenlegi kör számát
     * @return A jelenlegi kör száma
     */
    public int getCurrentTurn() {
        return currTurn;
    }

    /**
     * Visszaadja az összes kör számát
     * @return Az összes kör száma
     */
    public int getTotalTurns() {
        return totalTurns;
    }

    /**
     * Beállítja az összes kör számát
     * @param totalTurns Az összes kör új értéke
     */
    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }

    /**
     * Hozzáad egy fonalat a lebomlott fonalak listájához
     * @param hypha A lebomlott fonal
     */
    public void addDecayedHypha(Hypha hypha) {
        if (hypha != null && !decayedHypha.contains(hypha)) {
            decayedHypha.add(hypha);
        }
    }

    /**
     * Visszaadja a lebomlott fonalak listáját
     * @return A lebomlott fonalak listája
     */
    public List<Hypha> getDecayedHypha() {
        return decayedHypha;
    }

    public EntityRegistry getRegistry() {
        return registry;
    }

    public GameBoard getBoard() {
        return board;
    }

    public Logger getLogger() {
        return logger;
    }

    public Object getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Object selectedEntity) {
        this.selectedEntity = selectedEntity;
    }
}