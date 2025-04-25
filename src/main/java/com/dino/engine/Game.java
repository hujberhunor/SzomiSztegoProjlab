package com.dino.engine;
import java.util.*;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.player.Player;
import com.dino.tecton.Tecton;
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
    private int currRound;

    /**
     * Egy egész szám, ami eltárolja, hogy a játék hány kör után fog véget érni.
     * Értékét a felhasználók adják meg a meccs kezdete előtt.
     */
    private int totalRounds;

    /**
     * Az a játékos, aki éppen léphet.
     * Értéke a maximális mennyiségű akció felhasználása után a soron következő játékosra vált.
     */
    private Player currentPlayer;

    /**
     * Nem élő fonalak listája
     */
    private List<Hypha> decayedHypha;

    private Object selectedEntity;
    private EntityRegistry registry;
    private Logger logger;

    private Scanner scanner;

    public Game() {
        this.map = new GameBoard();
        this.players = new ArrayList<Player>();
        this.currRound = 0;
        this.totalRounds = 0;
        this.currentPlayer = null;
        this.decayedHypha = new ArrayList<>();
        this.registry = new EntityRegistry();
        this.logger = new Logger(registry);
        this.selectedEntity = null;
        this.scanner = new Scanner(System.in);
    }

    public Game(int totalRounds) {
        this.map = new GameBoard();
        this.players = new ArrayList<Player>();
        this.currRound = 0;
        this.totalRounds = totalRounds;
        this.currentPlayer = null;
        this.decayedHypha = new ArrayList<>();
        this.registry = new EntityRegistry();
        this.logger = new Logger(registry);
        this.selectedEntity = null;
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
        System.out.println("Adja meg a gombászok számát!");
        int numberOfMycologist = 0;
        while(numberOfMycologist < 2 || numberOfMycologist > 4) {
            numberOfMycologist = scanner.nextInt();
            scanner.nextLine();
            if(numberOfMycologist < 2) {
                System.out.println("A gombászok száma nem lehet kevesebb kettőnél! Adjon meg egy újat!");
            } else if (numberOfMycologist > 4) {
                System.out.println("A gombászok száma nem lehet több négynél! Adjon meg egy újat!");
            }
        }

        System.out.println("Adja meg a rovarászok számát!");
        int numberOfEntomologist = 0;
        while(numberOfEntomologist < 2 || numberOfEntomologist > 4 || numberOfEntomologist > numberOfMycologist) {
            numberOfEntomologist = scanner.nextInt();
            scanner.nextLine();
            if(numberOfEntomologist < 2) {
                System.out.println("A rovarászok száma nem lehet kevesebb kettőnél! Adjon meg egy újat!");
            } else if (numberOfEntomologist > 4) {
                System.out.println("A rovarászok száma nem lehet több négynél! Adjon meg egy újat!");
            } else if (numberOfEntomologist > numberOfMycologist) {
                System.out.println("Nem lehet több rovarász, mint gombász! Adjon meg egy újat!");
            }
        }

        for(int i = 0; i < numberOfMycologist; i++) {
            Mycologist mycologist = new Mycologist();
            players.add(mycologist);
            mycologist.setName("Gombász " + (i+1));
        }

        for(int i = 0; i < numberOfEntomologist; i++) {
            Entomologist entomologist = new Entomologist();
            players.add(entomologist);
            entomologist.setName("Rovarász " + (i+1));
        }

        System.out.println("Hány kör legyen a játék?");
        int numberOfRounds = 0;
        while(numberOfRounds < 1) {
            numberOfRounds = scanner.nextInt();
            scanner.nextLine();
            if(numberOfRounds < 1) {
                System.out.println("Legalább egy körnek lennie kell! Adjon meg egy újat!");
            }
        }

        totalRounds = numberOfRounds;
        return true;
    }

    /**
     * A játék első körtől való indításáért felelő függvény.
     */
    public void startGame() {
        List<Tecton> tectons = map.getTectons();
        List<Tecton> tectonsWithFungus = new ArrayList<>();

        int numberOfMycologist = 1;
        for(Player player : players) {
            if(player instanceof Mycologist) {
                System.out.println(numberOfMycologist + ". gombász\nMelyik tektonról szeretne indulni?\nVálasszon egy számot 1-től " + tectons.size() + "-ig");
                int selectedIndex = 0;
                while (selectedIndex < 1 || selectedIndex > tectons.size()) {
                    selectedIndex = scanner.nextInt();
                    scanner.nextLine();
                    if(selectedIndex < 1 || selectedIndex > tectons.size()) {
                        System.out.println("Helytelen szám! Válasszon egy számot 1-től " + tectons.size() + "-ig");
                    }
                }
                selectedIndex--;
                Tecton selectedTecton = tectons.get(selectedIndex);
                ((Mycologist) player).debugPlaceFungus(selectedTecton);
                tectons.remove(selectedIndex);
                tectonsWithFungus.add(selectedTecton);
                numberOfMycologist++;
            }
        }

        Random rnd = new Random();
        for(Player player : players) {
            if(player instanceof Entomologist) {
                int selectedIndex = rnd.nextInt(tectonsWithFungus.size());
                Insect insect = new Insect((Entomologist) player, tectonsWithFungus.get(selectedIndex));
                ((Entomologist) player).addInsects(insect);
                tectonsWithFungus.remove(selectedIndex);
            }
        }

        currentPlayer = players.get(0);
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

        if (validatePlayerRatio()) {
            int oldPlayerCount = players.size();
            players.add(player);

            logger.logChange("GAME", this, "PLAYERS_COUNT", String.valueOf(oldPlayerCount),
                    String.valueOf(players.size()));
        }
        else{
            logger.logError("GAME", "AddPlayer", "A játékos nem adható hozzá: a rovarászok száma nem lehet több, mint a gombászoké!");
            return false;
        }

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
        int oldPlayerCount = players.size();
        String playerName = registry.getNameOf(player);

        boolean result = players.remove(player);
        if (result) {
            logger.logChange("GAME", this, "PLAYERS_COUNT", String.valueOf(oldPlayerCount),
                    String.valueOf(players.size()));

            if (player == currentPlayer) {
                String oldPlayerName = playerName;

                if (players.isEmpty()) {
                    currentPlayer = null;
                    logger.logChange("GAME", this, "CURRENT_PLAYER", oldPlayerName, "null");
                } else {
                    // A következő játékos lesz az aktuális, vagy az első, ha ez volt az utolsó
                    int nextIndex = playerIndex % players.size();
                    currentPlayer = players.get(nextIndex);

                    String newPlayerName = registry.getNameOf(currentPlayer);
                    logger.logChange("GAME", this, "CURRENT_PLAYER", oldPlayerName, newPlayerName);
                }
            }
        }

        return result;
    }

    /**
     * Paraméter nélkül hívható függvény, ami lépteti a játékmenetet a következő játékosra.
     * Ha minden játékos sorra került, akkor meghívja a nextRound() függvényt.
     */
    public int nextTurn() {
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();

        CommandParser parser = new CommandParser(this);

        System.out.println("Aktuális játékos: " + currentPlayer.name);
        System.out.println("Készen állsz, gépelj commandokat (pl. MOVE_INSECT insect1 tectonB):");

        while (currentPlayer.remainingActions > 0) {
            String line = scanner.nextLine();
            if (line.isBlank())
                break;

            try {
                Command command = parser.parse(line);

                if(command.toString().equals("NEXT_TURN") || command.toString().equals("SKIP_TURN")) {
                    currentPlayer.remainingActions = 0;
                    command.execute(this, logger);
                    break;
                }

                if(command.toString().equals("NEXT_ROUND")) {
                    currentPlayer.remainingActions = 0;
                    nextIndex = 0;
                    command.execute(this, logger);
                    break;
                }

                if (command.validate(this)) {
                    command.execute(this, logger);
                    currentPlayer.decreaseActions();
                } else {
                    logger.logError("COMMAND", command.toString(), "Invalid command.");
                }
            } catch (Exception e) {
                logger.logError("COMMAND", line, "Parsing failed: " + e.getMessage());
            }
        }

        if (nextIndex == 0) {
            return 0;
        } else {
            String oldPlayerName = registry.getNameOf(currentPlayer);
            currentPlayer = players.get(nextIndex);
            currentPlayer.remainingActions = currentPlayer.actionsPerTurn;

            String newPlayerName = registry.getNameOf(currentPlayer);
            logger.logChange("GAME", this, "CURRENT_PLAYER", oldPlayerName, newPlayerName);

            return 1;
        }
    }

    /**
     * Paraméter nélkül hívható függvény, ami lépteti a játékmenetet a következő körre.
     * Meghívódik, amikor minden játékos befejezte a saját körét.
     */
    public int nextRound() {
        if (currRound == totalRounds) {
            endGame();
            return 0;
        }

        int oldRound = currRound;
        currRound++;

        logger.logChange("GAME", this, "ROUND", String.valueOf(oldRound), String.valueOf(currRound));

        map.breakHandler();

        String oldPlayerName = registry.getNameOf(currentPlayer);
        currentPlayer = players.get(0);
        currentPlayer.remainingActions = currentPlayer.actionsPerTurn;

        String newPlayerName = registry.getNameOf(currentPlayer);
        logger.logChange("GAME", this, "CURRENT_PLAYER", oldPlayerName, newPlayerName);

        return 1;
    }

    /**
     * Paraméter nélkül hívható függvény, ami befejezi a játékot.
     * Ez a függvény választja ki a két győztest a számontartott pontszámok alapján,
     * és akkor hívódik, amikor a currRound értéke eléri a totalRounds plusz egy értéket.
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
        return currRound;
    }

    /**
     * Visszaadja az összes kör számát
     * @return Az összes kör száma
     */
    public int getTotalRounds() {
        return totalRounds;
    }

    /**
     * Beállítja az összes kör számát
     * @param totalRounds Az összes kör új értéke
     */
    public void settotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
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
  
    public EntityRegistry getRegistry(){
        return registry;
    }

    public Object getSelectedEntity() {
        return selectedEntity;
    }
    
    public void setSelectedEntity(Object selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public GameBoard getBoard(){
        return map; 
    }

    public Logger getLogger(){
        return logger;
    }

    /**
     * Ellenőrzi, hogy a játékosok száma megfelel-e a szabályoknak:
     * a rovarászok száma nem lehet több, mint a gombászoké.
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
  
}