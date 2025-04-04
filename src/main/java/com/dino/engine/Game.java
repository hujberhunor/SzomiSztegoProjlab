package com.dino.engine;
import java.util.List;

import com.dino.player.Player;

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

    /**
     * Paraméter nélkül hívható függvény, ami legenerálja a játékteret.
     */
    public void initBoard() {}

    /**
     * Paraméter nélkül hívható függvény, ami a játék elemeinek inicializálásáért felel.
     * A már legenerált játéktérben a felhasználótól kapott bemenetek szerint felveszi,
     * és elhelyezi a játékosokat a kezdeti tektonokon, és inicializálja a játékmenet kezdeti értékeit.
     * @return A játék inicializálásának sikeressége.
     */
    public boolean initGame() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * A játék első körtől való indításáért felelő függvény.
     */
    public void startGame() {}

    /**
     * Felveszi az új paraméterként kapott játékost, és visszaadja, hogy a művelet sikeres volt-e.
     * @param player Felvenni kívánt új játékos.
     * @return Az új játékos felvételének sikeresége.
     */
    public boolean addPlayer(Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Törli a paraméterként kapott játékost, és visszaadja, hogy a művelet sikeres volt-e.
     * @param player Töröli kívánt játékos.
     * @return Az játékos törlésének sikeressége.
     */
    public boolean removePlayer(Player player) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Paraméter nélkül hívható függvény, ami lépteti a játékmenetet egy lépéssel.
     */
    public void nextTurn() {}

    /**
     * Paraméter nélkül hívható függvény, ami befejezi a játékot.
     * Ez a függvény választja ki a két győztest a számontartott pontszámok alapján,
     * és akkor hívódik, amikor a CurrTurn értéke eléri a TotalTurns plusz egy értéket.
     */
    public void endGame() {}
}
