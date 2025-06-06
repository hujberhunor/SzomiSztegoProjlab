package com.dino.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.commands.NextRoundCommand;
import com.dino.commands.NextTurnCommand;
import com.dino.commands.SkipTurnCommand;
import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.player.Player;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.view.GameEndDialog;
import com.dino.view.ModelObserver;

/**
 * A játékmenet alapvető funkcióit vezérlő, és annak tulajdonságait tároló és
 * kezelő osztály.
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
     * Egy egész szám, ami azt tartja számon, hogy a játék menete alatt hány kör
     * telt el.
     * Értéke eggyel nő, ha már minden játékos lépett.
     */
    private int currRound;

    /**
     * Egy egész szám, ami eltárolja, hogy a játék hány kör után fog véget érni.
     * Értékét a felhasználók adják meg a meccs kezdete előtt.
     */
    private int totalRounds;

    /**
     * Az a játékos, aki éppen léphet.
     * Értéke a maximális mennyiségű akció felhasználása után a soron következő
     * játékosra vált.
     */
    private Player currentPlayer;

    /**
     * Nem élő fonalak listája
     */
    private Map<Hypha, Integer> decayedHyphas;

    private Object selectedEntity;
    private EntityRegistry registry;
    private ObjectNamer namer;
    private Logger logger;

    private Scanner scanner;
    private static Game instance;

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    public Game() {
        this.map = new GameBoard();
        this.players = new ArrayList<Player>();
        this.currRound = 0;
        this.totalRounds = 0;
        this.currentPlayer = null;
        this.decayedHyphas = new HashMap<>();
        this.registry = EntityRegistry.getInstance();
        this.namer = ObjectNamer.getInstance();
        this.logger = Logger.getInstance();
        this.selectedEntity = null;
        this.scanner = new Scanner(System.in);
    }

    public Game(int totalRounds) {
        this.map = new GameBoard();
        this.players = new ArrayList<Player>();
        this.currRound = 0;
        this.totalRounds = totalRounds;
        this.currentPlayer = null;
        this.decayedHyphas = new HashMap<>();
        this.registry = EntityRegistry.getInstance();
        this.namer = ObjectNamer.getInstance();
        this.logger = Logger.getInstance();
        this.selectedEntity = null;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Paraméter nélkül hívható függvény, ami legenerálja a játékteret.
     */
    public void initBoard() {
        map.generateBoard();
    }

    /**
     * Paraméter nélkül hívható függvény, ami a játék elemeinek inicializálásáért
     * felel.
     * A már legenerált játéktérben a felhasználótól kapott bemenetek szerint
     * felveszi,
     * és elhelyezi a játékosokat a kezdeti tektonokon, és inicializálja a
     * játékmenet kezdeti értékeit.
     */
    public void initGame() {
        System.out.println("Adja meg a gombászok számát!");
        int numberOfMycologist = 0;
        while (numberOfMycologist < 2 || numberOfMycologist > 4) {
            numberOfMycologist = scanner.nextInt();
            scanner.nextLine();
            if (numberOfMycologist < 2) {
                System.out.println(
                        "A gombászok száma nem lehet kevesebb kettőnél! Adjon meg egy újat!");
            } else if (numberOfMycologist > 4) {
                System.out.println(
                        "A gombászok száma nem lehet több négynél! Adjon meg egy újat!");
            }
        }

        System.out.println("Adja meg a rovarászok számát!");
        int numberOfEntomologist = 0;
        while (numberOfEntomologist < 2 ||
                numberOfEntomologist > 4 ||
                numberOfEntomologist > numberOfMycologist) {
            numberOfEntomologist = scanner.nextInt();
            scanner.nextLine();
            if (numberOfEntomologist < 2) {
                System.out.println(
                        "A rovarászok száma nem lehet kevesebb kettőnél! Adjon meg egy újat!");
            } else if (numberOfEntomologist > 4) {
                System.out.println(
                        "A rovarászok száma nem lehet több négynél! Adjon meg egy újat!");
            } else if (numberOfEntomologist > numberOfMycologist) {
                System.out.println(
                        "Nem lehet több rovarász, mint gombász! Adjon meg egy újat!");
            }
        }

        for (int i = 0; i < numberOfMycologist; i++) {
            Mycologist mycologist = new Mycologist();
            players.add(mycologist);
            namer.register(mycologist);
            mycologist.setName(namer.getName(mycologist));
        }

        for (int i = 0; i < numberOfEntomologist; i++) {
            Entomologist entomologist = new Entomologist();
            players.add(entomologist);
            namer.register(entomologist);
            entomologist.setName(namer.getName(entomologist));
        }

        System.out.println("Hány kör legyen a játék?");
        int numberOfRounds = 0;
        while (numberOfRounds < 1) {
            numberOfRounds = scanner.nextInt();
            scanner.nextLine();
            if (numberOfRounds < 1) {
                System.out.println(
                        "Legalább egy körnek lennie kell! Adjon meg egy újat!");
            }
        }

        totalRounds = numberOfRounds;

        notifyObservers();
    }

    /**
     * A játék első körtől való indításáért felelő függvény.
     */
    public void startGame() {
        // Kiválogatjuk egy listába azokat a tektonokat, amikre lehet gombát helyezni
        List<Tecton> tectons = new ArrayList<>();
        for (Tecton t : map.getTectons()) {
            if (!(t instanceof NoFungiTecton))
                tectons.add(t);
        }

        List<Tecton> tectonsWithFungus = new ArrayList<>();

        int numberOfMycologist = 1;
        for (Player player : players) {
            if (player instanceof Mycologist) {
                System.out.println(
                        numberOfMycologist +
                                ". gombász\nMelyik tektonról szeretne indulni?\nVálasszon egy számot 1-től " +
                                tectons.size() +
                                "-ig");
                int selectedIndex = 0;
                while (selectedIndex < 1 || selectedIndex > tectons.size()) {
                    selectedIndex = scanner.nextInt();
                    scanner.nextLine();
                    if (selectedIndex < 1 || selectedIndex > tectons.size()) {
                        System.out.println(
                                "Helytelen szám! Válasszon egy számot 1-től " +
                                        tectons.size() +
                                        "-ig");
                    }
                }
                selectedIndex--;
                Tecton selectedTecton = tectons.get(selectedIndex);
                System.out.println("Kiválasztott tekton: " + namer.getName(selectedTecton));
                ((Mycologist) player).debugPlaceFungus(selectedTecton);
                tectons.remove(selectedIndex);
                tectonsWithFungus.add(selectedTecton);
                numberOfMycologist++;
                System.out.println("Gomba: " + namer.getName(((Mycologist) player).getMushrooms().get(0)) + "\n");
            }
        }

        Random rnd = new Random();
        for (Player player : players) {
            if (player instanceof Entomologist) {
                int selectedIndex = rnd.nextInt(tectonsWithFungus.size());
                Insect insect = new Insect(
                        (Entomologist) player,
                        tectonsWithFungus.get(selectedIndex));
                namer.register(insect);
                System.out.println(
                        "Insect regisztrálva, neve:" + namer.getName(insect) + "\nKezdő tekton:" + insect.getTecton()
                                + "\n");
                tectonsWithFungus.remove(selectedIndex);
            }
        }

        currentPlayer = players.get(0);
    }

    public void gameplay() {
        int endOfRound = 1;
        while (endOfRound != 0) {
            endOfRound = nextTurn();
        }

        // After first round is complete
        if (totalRounds > 1) {
            boolean gameIsEnded = false;
            for (int currentRound = 1; currentRound < totalRounds && !gameIsEnded; currentRound++) {
                int endOfGame = nextRound();
                if (endOfGame == 0) {
                    gameIsEnded = true; // Game has ended prematurely
                    break;
                }

                endOfRound = 1; // Reset for the new round
                while (endOfRound != 0) {
                    endOfRound = nextTurn();
                }
            }

            // Make sure we call endGame() if we exited the loop normally
            if (!gameIsEnded) {
                endGame();
            }
        } else {
            endGame();
        }
    }

    // MAIN 9-es teszt erre dependál
    public boolean TSTinitGame() {
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

        currRound = 0;
        decayedHyphas.clear();

        return true;
    }

    /**
     * A játék első körtől való indításáért felelő függvény.
     */
    public void TSTstartGame() {
        currRound = 1;

        if (currentPlayer == null && !players.isEmpty()) {
            currentPlayer = players.get(0);
        }

        for (Player player : players) {
            player.remainingActions = player.actionsPerTurn;
        }
    }

    /**
     * Felveszi az új paraméterként kapott játékost, és visszaadja, hogy a művelet
     * sikeres volt-e.
     *
     * @param player Felvenni kívánt új játékos.
     * @return Az új játékos felvételének sikeresége.
     */

    public boolean addPlayer(Player player) {
        if (player == null || players.contains(player)) {
            return false;
        }

        if (validatePlayerRatio()) {
            int oldPlayerCount = players.size();
            players.add(player);

            logger.logOk(
                    "GAME",
                    "null",
                    "PLAYERS_COUNT",
                    String.valueOf(oldPlayerCount),
                    String.valueOf(players.size()));
        } else {
            logger.logError(
                    "GAME",
                    "AddPlayer",
                    "A játékos nem adható hozzá: a rovarászok száma nem lehet több, mint a gombászoké!");
            return false;
        }

        return true;
    }

    /**
     * Paraméter nélkül hívható függvény, ami lépteti a játékmenetet a következő
     * játékosra.
     * Ha minden játékos sorra került, akkor meghívja a nextRound() függvényt.
     */
    public int nextTurn() {
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();
        boolean roundEnded = false;

        System.out.println("Aktuális játékos: " + namer.getName(currentPlayer));
        System.out.println(
                "Készen állsz, gépelj commandokat (pl. MOVE_INSECT insect1 tectonB):");

        // Scanner kezelése külön metódusba kerül át
        roundEnded = processPlayerCommands();

        // Ha next_round kommandot írunk be, vagy ha az utolsó játékos volt soron,
        // legyen vége a roundnak
        if (roundEnded || nextIndex == 0) {
            return 0; // Jelezzük, hogy kör vége
        } else {
            String oldPlayerName = namer.getName(currentPlayer);
            currentPlayer = players.get(nextIndex);
            currentPlayer.remainingActions = currentPlayer.actionsPerTurn;

            // Log üzenet
            String newPlayerName = namer.getName(currentPlayer);
            logger.logChange(
                    "GAME",
                    this,
                    "CURRENT_PLAYER",
                    oldPlayerName,
                    newPlayerName);

            return 1;
        }
    }

    /**
     * Játékos commandjainak feldolgozása
     * 
     * @return true, ha körváltás (NEXT_ROUND) történt, false egyébként
     */
    private boolean processPlayerCommands() {
        while (currentPlayer.remainingActions > 0) {
            String line = scanner.nextLine();
            if (line.isBlank())
                break;

            Command command = null;
            try {
                // A command parserelése a CommandParser osztály feladata
                CommandParser parser = new CommandParser(this);
                command = parser.parse(line);

                if (command.validate(this)) {
                    // Csak az execute-ot hívjuk meg
                    command.execute(this, logger);

                    // Speciális parancsok kezelése
                    if (command instanceof NextTurnCommand ||
                            command instanceof SkipTurnCommand) {
                        return false; // Vége a körnek, de nem a rundnak
                    } else if (command instanceof NextRoundCommand) {
                        return true; // Vége a rundnak
                    } else {
                        // Minden más command elhasznál egy akciót
                        // currentPlayer.decreaseActions();
                    }
                } else {
                    logger.logError(
                            "COMMAND",
                            command.toString(),
                            "Invalid command.");
                }
            } catch (Exception e) {
                logger.logError(
                        "COMMAND",
                        line,
                        "Parsing failed: " + e.getMessage());
            }
        }

        return false; // Alapértelmezetten nem váltunk rundot
    }

    /**
     * Paraméter nélkül hívható függvény, ami lépteti a játékmenetet a következő
     * körre.
     * Meghívódik, amikor minden játékos befejezte a saját körét.
     */
    public int nextRound() {
        if (currRound >= totalRounds) {
            return 0; // Jelzés, hogy a játéknak vége
        }

        int oldRound = currRound;
        currRound++;

        logger.logChange(
                "GAME",
                this,
                "ROUND",
                String.valueOf(oldRound),
                String.valueOf(currRound));

        map.breakHandler();

        for (Player player : players) {
            if (player instanceof Mycologist) {
                for (Fungus f : ((Mycologist) player).getMushrooms()) {
                    int oldCharge = f.getCharge();
                    f.increaseCharge();
                    logger.logChange("FUNGUS", f, "CHARGE", oldCharge, f.getCharge());
                }
            }
            else if(player instanceof Entomologist) {
                for (Insect insect : ((Entomologist) player).getInsects()) {
                    for (Spore effekt : insect.getEffects()) {
                        effekt.decreaseEffectDuration();
                    }
                    insect.removeExpiredEffects();
                }
            }
        }

        String oldPlayerName = namer.getName(currentPlayer);
        currentPlayer = players.get(0);
        currentPlayer.remainingActions = currentPlayer.actionsPerTurn;

        String newPlayerName = namer.getName(currentPlayer);
        logger.logChange(
                "GAME",
                this,
                "CURRENT_PLAYER",
                oldPlayerName,
                newPlayerName);

        return 1;
    }

    /**
     * Paraméter nélkül hívható függvény, ami befejezi a játékot.
     * Ez a függvény választja ki a két győztest a számontartott pontszámok alapján,
     * és akkor hívódik, amikor a currRound értéke eléri a totalRounds plusz egy
     * értéket.
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
            System.out.println(
                    (i + 1) +
                            ". helyezett: " +
                            player.name +
                            " - Pontszám: " +
                            player.score);
        }

        // Értesítjük az observer-eket, hogy a játék véget ért
        notifyObservers();

        // Ha JavaFX környezetben vagyunk, GUI dialógust is megjelenítünk
        try {
            javafx.application.Platform.runLater(() -> {
                GameEndDialog dialog = new GameEndDialog(players);
                dialog.showAndWait();
            });
        } catch (Exception e) {
            // Ha nincs JavaFX környezet (pl. konzol módban futunk),
            // akkor csak ignoráljuk a hibát
            System.out.println("(GUI nem elérhető, csak konzolos megjelenítés)");
        }
    }

    /**
     * Visszaadja a jelenlegi játékost
     *
     * @return Az aktuális játékos
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Visszaadja a játéktáblát
     *
     * @return A játéktábla objektum
     */
    public GameBoard getMap() {
        return map;
    }

    /**
     * Visszaadja a játékosok listáját
     *
     * @return A játékosok listája
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Visszaadja a jelenlegi kör számát
     *
     * @return A jelenlegi kör száma
     */
    public int getCurrentRound() {
        return currRound;
    }

    /**
     * Visszaadja az összes kör számát
     *
     * @return Az összes kör száma
     */
    public int getTotalRounds() {
        return totalRounds;
    }

    /**
     * Beállítja az összes kör számát
     *
     * @param totalRounds Az összes kör új értéke
     */
    public void settotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    /**
     * Hozzáad egy fonalat a lebomlott fonalak listájához
     *
     * @param hypha A lebomlott fonal
     */
    public void addDecayedHypha(Hypha hypha) {
        if (hypha == null) {
            return;
        }

        if (!decayedHyphas.containsKey(hypha)) {
            decayedHyphas.put(hypha, 3);
        }
    }

    public void decayHyphas() {
        for (Map.Entry<Hypha, Integer> entry : decayedHyphas.entrySet()) {
            int currentDecay = entry.getValue();
            if (currentDecay <= 0) {
                entry.getKey().destroyHypha();
            } else {
                entry.setValue(currentDecay - 1);
            }
        }
    }

    /**
     * Visszaadja a lebomlott fonalak listáját
     *
     * @return A lebomlott fonalak listája
     */
    public Map<Hypha, Integer> getDecayedHyphas() {
        return decayedHyphas;
    }

    public EntityRegistry getRegistry() {
        return registry;
    }

    public Object getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Object selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public GameBoard getBoard() {
        return map;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Ellenőrzi, hogy a játékosok száma megfelel-e a szabályoknak:
     * a rovarászok száma nem lehet több, mint a gombászoké.
     * 
     * @return true, ha a játékosok aránya megfelelő, false ha nem
     */
    public boolean validatePlayerRatio() {
        int entomologistCount = 0;
        int mycologistCount = 0;

        for (Player player : players) {
            if (player instanceof Entomologist) {
                entomologistCount++;
            } else
                mycologistCount++;
        }

        return entomologistCount <= mycologistCount;
    }

    // szerializálás
    public List<Mycologist> getAllMycologists() {
        List<Mycologist> result = new ArrayList<>();
        for (Player p : players) {
            if (p instanceof Mycologist)
                result.add((Mycologist) p);
        }
        return result;
    }

    public List<Entomologist> getAllEntomologists() {
        List<Entomologist> result = new ArrayList<>();
        for (Player p : players) {
            if (p instanceof Entomologist)
                result.add((Entomologist) p);
        }
        return result;
    }

    public ObjectNamer getNamer() {
        return namer;
    }

    public void setCurrentTurn(int asInt) {
        this.currRound = asInt;
    }

    public void setCurrentPlayer(Player byName) {
        this.currentPlayer = byName;
    }

    private final List<ModelObserver> observers = new ArrayList<>();

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (ModelObserver obs : observers) {
            obs.update(this);
        }
    }

    public boolean quickInit() {
        int numberOfMycologist = 4;
        int numberOfEntomologist = 4;
        int numberOfRounds = 5;

        Random random = new Random();
        Tecton targTecton = map.getTectons().get(random.nextInt(map.getTectons().size()));

        // Tecton.connectTectons(targTecton,
        // map.getTectons().get(random.nextInt(map.getTectons().size())));
        // Tecton.connectWithHypha(targTecton,
        // map.getTectons().get(random.nextInt(map.getTectons().size())));

        for (int i = 0; i < numberOfMycologist; i++) {
            Mycologist mycologist = new Mycologist();
            players.add(mycologist);
            namer.register(mycologist);
            mycologist.placeFungus(targTecton);
            // Random random = new Random();
            // mycologist.placeFungus(map.getTectons().get(random.nextInt(map.getTectons().size())));
        }

        for (int i = 0; i < numberOfEntomologist; i++) {
            Entomologist entomologist = new Entomologist();
            players.add(entomologist);
            namer.register(entomologist);
            // Random random = new Random();
            // Tecton targTecton =
            // map.getTectons().get(random.nextInt(map.getTectons().size()));
            Insect insect = new Insect(entomologist, targTecton);
            targTecton.getInsects().add(insect);
        }

        totalRounds = numberOfRounds;

        currentPlayer = players.get(0);

        notifyObservers();

        return true;
    }

    public List<ModelObserver> getObservers() {
        return observers;
    }

    private boolean autoAdvanceInProgress = false;

    public void autoAdvanceIfNeeded() {
        if (autoAdvanceInProgress)
            return;
        autoAdvanceInProgress = true;

        while (currentPlayer.remainingActions <= 0) {
            int turnResult = nextTurn();

            if (turnResult == 0) {
                int roundResult = nextRound();
                if (roundResult == 0) {
                    endGame();
                    break;
                }
            }
        }

        notifyObservers();
        autoAdvanceInProgress = false;
    }

}
