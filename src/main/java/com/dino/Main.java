package com.dino;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.core.Fungus;
import com.dino.core.Hexagon;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.effects.AcceleratingEffect;
import com.dino.effects.ParalyzingEffect;
import com.dino.effects.StunningEffect;
import com.dino.engine.Game;
import com.dino.engine.GameBoard;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.tecton.InfiniteHyphaTecton;
import com.dino.tecton.KeepHyphaTecton;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.SingleHyphaTecton;
import com.dino.tecton.Tecton;
import com.dino.tests.TestOracle;
import com.dino.util.EntityRegistry;
import com.dino.util.InitLoader;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.Serializer;
import com.dino.util.Skeleton;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void SimpleSerializeTest() {
        try {
            ObjectNamer namer = ObjectNamer.getInstance();

            // Dummy Mycologist
            Mycologist m = new Mycologist();
            namer.register(m);

            // Dummy Tecton
            Tecton t = new SingleHyphaTecton();
            t.hexagons.add(new Hexagon(10));
            t.hexagons.add(new Hexagon(11));
            namer.register(t);

            // Dummy Fungus
            Fungus f = new Fungus(m, t);
            namer.register(f);
            t.setFungus(f);

            // Mentés
            Serializer.saveToFile(t, namer, "simple_tecton_save.json");

            System.out.println("SimpleSerializeTest: Sikeres mentés!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void ComplexSerializeTest() {
        try {
            ObjectNamer namer = ObjectNamer.getInstance();

            // Mycologists + Entomologists
            Mycologist m1 = new Mycologist();
            Mycologist m2 = new Mycologist();
            Entomologist e1 = new Entomologist();
            Entomologist e2 = new Entomologist();
            namer.register(m1);
            namer.register(m2);
            namer.register(e1);
            namer.register(e2);

            // Tectons
            Tecton t1 = new KeepHyphaTecton();
            Tecton t2 = new NoFungiTecton();
            t1.hexagons.add(new Hexagon(21));
            t1.hexagons.add(new Hexagon(22));
            t2.hexagons.add(new Hexagon(23));
            t2.hexagons.add(new Hexagon(24));
            namer.register(t1);
            namer.register(t2);

            // Fungus on t1
            Fungus f1 = new Fungus(m1, t1);
            namer.register(f1);
            t1.setFungus(f1);

            // Hypha connecting t1 and t2
            Hypha h1 = new Hypha(m1, f1);
            h1.continueHypha(t2);
            t1.hyphas.add(h1);
            t2.hyphas.add(h1);
            namer.register(h1);

            // Insects on t1 and t2
            Insect insect1 = new Insect(e1, t1);
            insect1.getEffects().add(new AcceleratingEffect(m1));
            namer.register(insect1);
            t1.insects.add(insect1);

            Insect insect2 = new Insect(e2, t2);
            insect2.getEffects().add(new ParalyzingEffect(m2));
            namer.register(insect2);
            t2.insects.add(insect2);

            ParalyzingEffect s1 = new ParalyzingEffect(m1);
            ParalyzingEffect s2 = new ParalyzingEffect(m2);
            // Spóra számlálók
            t1.spores.put(s1, 2);
            t2.spores.put(s2, 1);

            // Neighbours
            t1.neighbours.add(t2);
            t2.neighbours.add(t1);

            // Mentés
            Serializer.saveToFile(t1, namer, "complex_tecton_save.json");
            Serializer.saveToFile(t2, namer, "complex_tecton2_save.json");

            System.out.println("ComplexSerializeTest: Sikeres mentés!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void FullGameSerializeTest() {
        try {
            // Új Game példány
            Game game = new Game(10); // 10 körös játék
            ObjectNamer namer = game.getNamer(); // <<< a Game saját namer-jét használjuk

            // --- Játéktér generálása ---
            game.initBoard(); // <<< ez automatikusan legenerálja a tectonokat és hexagonokat

            // --- Tectonok regisztrálása a namer-be ---
            for (Tecton t : game.getBoard().getAllTectons()) {
                namer.register(t);
            }

            // --- Játékosok létrehozása és regisztrálása ---
            Mycologist m1 = new Mycologist();
            Entomologist e1 = new Entomologist();
            game.addPlayer(m1);
            game.addPlayer(e1);
            namer.register(m1);
            namer.register(e1);

            // --- Játék inicializálása ---
            game.TSTinitGame();
            game.TSTstartGame();

            // --- Mentés ---
            JsonObject gameState = InitLoader.serialize(game, namer);
            Serializer.saveJsonToFile(gameState, "full_game_save.json");

            System.out.println("FullGameSerializeTest: Sikeres mentés!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * A commandok beolvasásáért felel
     *
     * @param game
     * @param logger
     */
    static void scanner(Game game, Logger logger) {
        Scanner scanner = new Scanner(System.in);
        CommandParser parser = new CommandParser(game);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Command command;

            try {
                command = parser.parse(line);

                if (command.validate(game)) { // valid-e a command
                    command.execute(game, logger);
                } else {
                    logger.logError(
                        "COMMAND",
                        command.toString(),
                        "Invalid command."
                    );
                }
            } catch (Exception e) {
                logger.logError(
                    "COMMAND",
                    line,
                    "Parsing failed: " + e.getMessage()
                );
            }
        }
    }

    public static void testCommand() {
        Game game = new Game(3);
        EntityRegistry registry = game.getRegistry();
        Logger logger = game.getLogger();
        GameBoard board = game.getBoard();

        Tecton t1 = new NoFungiTecton();
        Tecton t2 = new NoFungiTecton();
        board.connect(t1, t2);

        Entomologist entomologist = new Entomologist();
        Insect insect = new Insect(entomologist, t1);

        registry.register("tectonA", t1);
        registry.register("tectonB", t2);
        registry.register("insect1", insect);

        System.out.println("Hyphák száma t1-en: " + t1.getHyphas().size());
        System.out.println("t1 szomszédai: " + t1.getNeighbours().size());
        System.out.println("t1 és t2 között van fonál? " + t1.hasHypha(t2));

        Scanner inputScanner = new Scanner(System.in);
        CommandParser parser = new CommandParser(game);

        System.out.println(
            "Készen állsz, gépelj commandokat (pl. MOVE_INSECT insect1 tectonB):"
        );

        while (inputScanner.hasNextLine()) {
            String line = inputScanner.nextLine();
            if (line.isBlank()) break;

            try {
                Command command = parser.parse(line);
                if (command.validate(game)) {
                    command.execute(game, logger);
                } else {
                    logger.logError(
                        "COMMAND",
                        command.toString(),
                        "Invalid command."
                    );
                }
            } catch (Exception e) {
                logger.logError(
                    "COMMAND",
                    line,
                    "Parsing failed: " + e.getMessage()
                );
            }
        }

        inputScanner.close();
    }

    public static void FullGameDeserializeTest() {
        try {
            System.out.println("Betöltés elindult...");

            Game loadedGame = InitLoader.loadFromFile("full_game_save.json");

            System.out.println("Betöltés sikeres!");
            System.out.println(
                "Játékosok száma: " + loadedGame.getPlayers().size()
            );
            System.out.println(
                "Tectonok száma: " +
                loadedGame.getBoard().getAllTectons().size()
            );

            if (loadedGame.getCurrentPlayer() != null) {
                System.out.println(
                    "Jelenlegi játékos: " + loadedGame.getCurrentPlayer().name
                );
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void stage2Main() {
        Game game = new Game();
        EntityRegistry registry = game.getRegistry();
        Logger logger = game.getLogger();

        game.initBoard();
        game.initGame();
        game.startGame();

        int endOfRound = 1;
        while (endOfRound != 0) {
            endOfRound = game.nextTurn();
        }

        // After first round is complete
        if (game.getTotalRounds() > 1) {
            boolean gameIsEnded = false;
            for (
                int currentRound = 1;
                currentRound < game.getTotalRounds() && !gameIsEnded;
                currentRound++
            ) {
                int endOfGame = game.nextRound();
                if (endOfGame == 0) {
                    gameIsEnded = true; // Game has ended prematurely
                    break;
                }

                endOfRound = 1; // Reset for the new round
                while (endOfRound != 0) {
                    endOfRound = game.nextTurn();
                }
            }

            // Make sure we call endGame() if we exited the loop normally
            if (!gameIsEnded) {
                game.endGame();
            }
        } else {
            game.endGame();
        }
    }

    private static void runTestOracleMenu() {
        Scanner testScanner = new Scanner(System.in);

        System.out.println("Tesztorákulum futtatása:");
        System.out.println("1. Összes teszt futtatása");
        System.out.println("2. Egy adott teszt futtatása");
        System.out.print("Válassz: ");

        int choice = testScanner.nextInt();

        switch (choice) {
            case 1:
                com.dino.tests.TestRunner.runAllTests();
                break;
            case 2:
                System.out.print("Add meg a teszt számát (pl. 0, 9, 10...): ");
                int testNumber = testScanner.nextInt();
                com.dino.tests.TestRunner.runSingleTest(testNumber);
                break;
            default:
                System.out.println("Érvénytelen választás.");
                break;
        }
    }

    private static final String TEST_FILE = "serialization_test.json";

    public static void cloudeTest(String[] args) {
        try {
            // Run the test and report results
            boolean success = runSerializationTest();
            System.out.println(
                "\nTest result: " + (success ? "PASSED" : "FAILED")
            );
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean runSerializationTest() {
        try {
            // Reset any existing state
            EntityRegistry.reset();
            ObjectNamer.reset();

            // Step 1: Create a simple game state
            System.out.println("Creating test game state...");
            Game originalGame = createTestGame();
            printGameState(originalGame, "Original");

            // Step 2: Serialize the game state
            System.out.println("\nSerializing game state...");
            JsonObject jsonState = InitLoader.serialize(
                originalGame,
                ObjectNamer.getInstance()
            );
            Serializer.saveJsonToFile(jsonState, TEST_FILE);
            System.out.println("Game state saved to " + TEST_FILE);

            // Step 3: Deserialize the game state
            System.out.println("\nDeserializing game state...");
            Game loadedGame = InitLoader.loadFromFile(TEST_FILE);
            printGameState(loadedGame, "Loaded");

            // Step 4: Verify the loaded state
            boolean success = verifyGameState(originalGame, loadedGame);

            // Clean up test file
            Files.deleteIfExists(Paths.get(TEST_FILE));

            return success;
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a simple test game with some basic entities.
     */
    private static Game createTestGame() {
        // Create a game with 3 rounds
        Game game = new Game(3);

        // Add a mycologist and an entomologist
        Mycologist mycologist = new Mycologist();
        mycologist.score = 10;
        mycologist.setName("Test Mycologist");

        Entomologist entomologist = new Entomologist();
        entomologist.score = 15;
        entomologist.setName("Test Entomologist");

        game.addPlayer(mycologist);
        game.addPlayer(entomologist);

        // Set up some tectons
        Tecton tecton1 = new SingleHyphaTecton();
        Tecton tecton2 = new NoFungiTecton();

        // Connect the tectons
        Tecton.connectTectons(tecton1, tecton2);

        // Add tectons to the game board
        game.getBoard().getAllTectons().add(tecton1);
        game.getBoard().getAllTectons().add(tecton2);

        // Register all entities with the namer
        ObjectNamer namer = ObjectNamer.getInstance();
        namer.register(mycologist);
        namer.register(entomologist);
        namer.register(tecton1);
        namer.register(tecton2);

        // Create a fungus and place it on tecton1
        Fungus fungus = new Fungus(mycologist, tecton1);
        fungus.setCharge(2);
        namer.register(fungus);
        tecton1.setFungus(fungus);
        mycologist.getMushrooms().add(fungus);

        // Create an insect and place it on tecton2
        Insect insect = new Insect(entomologist, tecton2);
        namer.register(insect);
        tecton2.getInsects().add(insect);
        entomologist.getInsects().add(insect);

        // Set current player and round
        game.setCurrentPlayer(mycologist);
        game.setCurrentTurn(1);

        return game;
    }

    /**
     * Prints the current state of a game object for debugging.
     */
    private static void printGameState(Game game, String label) {
        System.out.println("===== " + label + " Game State =====");
        System.out.println("Total rounds: " + game.getTotalRounds());
        System.out.println("Current round: " + game.getCurrentRound());

        if (game.getCurrentPlayer() != null) {
            System.out.println(
                "Current player: " + game.getCurrentPlayer().name
            );
        } else {
            System.out.println("Current player: null");
        }

        System.out.println("Players: " + game.getPlayers().size());
        for (int i = 0; i < game.getPlayers().size(); i++) {
            System.out.println(
                "  Player " +
                (i + 1) +
                ": " +
                game.getPlayers().get(i).name +
                " (Score: " +
                game.getPlayers().get(i).score +
                ")"
            );
        }

        System.out.println(
            "Tectons: " + game.getBoard().getAllTectons().size()
        );

        // Count fungi and insects
        int fungiCount = 0;
        int insectCount = 0;
        for (Tecton t : game.getBoard().getAllTectons()) {
            if (t.getFungus() != null) fungiCount++;
            insectCount += t.getInsects().size();
        }
        System.out.println("Fungi: " + fungiCount);
        System.out.println("Insects: " + insectCount);
    }

    /**
     * Verifies that the loaded game state matches the original.
     */
    private static boolean verifyGameState(Game original, Game loaded) {
        System.out.println("\n===== Verification Results =====");

        boolean success = true;

        // Check basic game properties
        if (original.getTotalRounds() != loaded.getTotalRounds()) {
            System.out.println(
                "❌ Total rounds mismatch: Original=" +
                original.getTotalRounds() +
                ", Loaded=" +
                loaded.getTotalRounds()
            );
            success = false;
        } else {
            System.out.println(
                "✓ Total rounds match: " + original.getTotalRounds()
            );
        }

        if (original.getCurrentRound() != loaded.getCurrentRound()) {
            System.out.println(
                "❌ Current round mismatch: Original=" +
                original.getCurrentRound() +
                ", Loaded=" +
                loaded.getCurrentRound()
            );
            success = false;
        } else {
            System.out.println(
                "✓ Current round match: " + original.getCurrentRound()
            );
        }

        // Check player count
        if (original.getPlayers().size() != loaded.getPlayers().size()) {
            System.out.println(
                "❌ Player count mismatch: Original=" +
                original.getPlayers().size() +
                ", Loaded=" +
                loaded.getPlayers().size()
            );
            success = false;
        } else {
            System.out.println(
                "✓ Player count match: " + original.getPlayers().size()
            );
        }

        // Check tecton count
        if (
            original.getBoard().getAllTectons().size() !=
            loaded.getBoard().getAllTectons().size()
        ) {
            System.out.println(
                "❌ Tecton count mismatch: Original=" +
                original.getBoard().getAllTectons().size() +
                ", Loaded=" +
                loaded.getBoard().getAllTectons().size()
            );
            success = false;
        } else {
            System.out.println(
                "✓ Tecton count match: " +
                original.getBoard().getAllTectons().size()
            );
        }

        // Count fungi and insects in both games
        int originalFungiCount = 0;
        int originalInsectCount = 0;
        for (Tecton t : original.getBoard().getAllTectons()) {
            if (t.getFungus() != null) originalFungiCount++;
            originalInsectCount += t.getInsects().size();
        }

        int loadedFungiCount = 0;
        int loadedInsectCount = 0;
        for (Tecton t : loaded.getBoard().getAllTectons()) {
            if (t.getFungus() != null) loadedFungiCount++;
            loadedInsectCount += t.getInsects().size();
        }

        if (originalFungiCount != loadedFungiCount) {
            System.out.println(
                "❌ Fungi count mismatch: Original=" +
                originalFungiCount +
                ", Loaded=" +
                loadedFungiCount
            );
            success = false;
        } else {
            System.out.println("✓ Fungi count match: " + originalFungiCount);
        }

        if (originalInsectCount != loadedInsectCount) {
            System.out.println(
                "❌ Insect count mismatch: Original=" +
                originalInsectCount +
                ", Loaded=" +
                loadedInsectCount
            );
            success = false;
        } else {
            System.out.println("✓ Insect count match: " + originalInsectCount);
        }

        // Check if tecton connections are maintained
        boolean originalConnectionExists = false;
        boolean loadedConnectionExists = false;

        if (original.getBoard().getAllTectons().size() >= 2) {
            Tecton t1 = original.getBoard().getAllTectons().get(0);
            Tecton t2 = original.getBoard().getAllTectons().get(1);
            originalConnectionExists =
                t1.getNeighbours().contains(t2) &&
                t2.getNeighbours().contains(t1);
        }

        if (loaded.getBoard().getAllTectons().size() >= 2) {
            Tecton t1 = loaded.getBoard().getAllTectons().get(0);
            Tecton t2 = loaded.getBoard().getAllTectons().get(1);
            loadedConnectionExists =
                t1.getNeighbours().contains(t2) &&
                t2.getNeighbours().contains(t1);
        }

        if (originalConnectionExists != loadedConnectionExists) {
            System.out.println(
                "❌ Tecton connection mismatch: Original=" +
                originalConnectionExists +
                ", Loaded=" +
                loadedConnectionExists
            );
            success = false;
        } else {
            System.out.println("✓ Tecton connections preserved");
        }

        return success;
    }

    // -------------------------------- //
    public static void main(String[] args) {
        boolean menuActive = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 0 to exit\n");

        while (menuActive) {
            System.out.println("-----------------------\nUse case list:");
            System.out.println("1. Insect movement");
            System.out.println("2. Insect eating");
            System.out.println("3. Insect cutting");
            System.out.println("4. Place fungus");
            System.out.println("5. Spread spore");
            System.out.println("6. Grow hypha");
            System.out.println("7. Tecton splitting");
            System.out.println("8. Logger teszt");
            System.out.println("9. Serializáció teszt");
            System.out.println("10. Scanner teszt");
            System.out.println("11. Full gameplay");
            System.out.println("12. Deszerializálás");
            System.out.println("13. Tesztek futtatása");
            System.out.println("-----------------------");
            System.out.print("Select use case (e.g. 1, 2...): ");
            int useCase = scanner.nextInt();
            switch (useCase) {
                case 0:
                    menuActive = false;
                    scanner.close();
                    break;
                case 9:
                    // SimpleSerializeTest();
                    // ComplexSerializeTest();
                    FullGameSerializeTest();
                    break;
                case 10:
                    testCommand();
                    break;
                case 11:
                    stage2Main();
                    break;
                case 12:
                    FullGameDeserializeTest();
                    break;
                case 13:
                    runTestOracleMenu();
                    break;
                case 14:
                    cloudeTest(args);
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
            System.out.println("");
        }
    }
}
