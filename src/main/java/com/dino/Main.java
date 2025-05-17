package com.dino;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.core.Fungus;
import com.dino.core.Hexagon;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.effects.AcceleratingEffect;
import com.dino.effects.ParalyzingEffect;
import com.dino.engine.Game;
import com.dino.engine.GameBoard;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.tecton.InfiniteHyphaTecton;
import com.dino.tecton.KeepHyphaTecton;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.ShortHyphaTecton;
import com.dino.tecton.SingleHyphaTecton;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.InitLoader;
import com.dino.util.Logger;
import com.dino.util.ObjectNamer;
import com.dino.util.Serializer;
import com.google.gson.JsonObject;

import javafx.application.Application;

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

            // Spóra számlálók

            Spore s1 = f1.createRandomSpore();
            Spore s2 = f1.createRandomSpore();
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
                    logger.logError("COMMAND", command.toString(), "Invalid command.");
                }

            } catch (Exception e) {
                logger.logError("COMMAND", line, "Parsing failed: " + e.getMessage());
            }
        }
    }

    public static void testAllCommands() {
        Game game = new Game(3);
        EntityRegistry registry = game.getRegistry();
        Logger logger = game.getLogger();
        GameBoard board = game.getBoard();

        // Set up test environment
        System.out.println("Setting up test environment...");

        // Create tectons
        Tecton t1 = new NoFungiTecton();
        Tecton t2 = new InfiniteHyphaTecton();
        Tecton t3 = new KeepHyphaTecton();
        board.connect(t1, t2);
        board.connect(t2, t3);

        // Register tectons
        registry.register("tectonA", t1);
        registry.register("tectonB", t2);
        registry.register("tectonC", t3);

        // Create and register players
        Mycologist mycologist = new Mycologist();
        Entomologist entomologist = new Entomologist();
        registry.register("myco1", mycologist);
        registry.register("ento1", entomologist);
        game.addPlayer(mycologist);
        game.addPlayer(entomologist);

        // Create and register insect
        Insect insect = new Insect(entomologist, t1);
        registry.register("insect1", insect);

        // Place fungus
        Fungus fungus = new Fungus(mycologist, t2);
        t2.setFungus(fungus);
        registry.register("fungus1", fungus);

        // Create hypha
        Hypha hypha = new Hypha(mycologist, fungus);
        hypha.continueHypha(t1);
        t1.hyphas.add(hypha);
        t2.hyphas.add(hypha);
        registry.register("hypha1", hypha);

        // Add spores
        Spore spore = fungus.createRandomSpore();
        t1.spores.put(spore, 2);

        // Print initial state
        System.out.println("Tectons: ");
        System.out.println("- " + registry.getNameOf(t1));
        System.out.println("- " + registry.getNameOf(t2));
        System.out.println("- " + registry.getNameOf(t3));

        System.out.println("Players: ");
        System.out.println("- " + registry.getNameOf(mycologist) + " (Mycologist)");
        System.out.println("- " + registry.getNameOf(entomologist) + " (Entomologist)");

        System.out.println("Insect: " + registry.getNameOf(insect) +
                " on " + registry.getNameOf(insect.getTecton()));

        System.out.println("Fungus: " + registry.getNameOf(fungus) +
                " on " + registry.getNameOf(fungus.getTecton()));

        System.out.println("Hypha: " + registry.getNameOf(hypha) +
                " connects: " + registry.getNameOf(t1) +
                " <-> " + registry.getNameOf(t2));

        System.out.println("Spores on " + registry.getNameOf(t1) +
                ": " + t1.spores.size());

        // Command parser
        CommandParser parser = new CommandParser(game);
        Scanner inputScanner = new Scanner(System.in);

        // List available commands
        System.out.println("\nAvailable commands to test:");
        System.out.println("1. MOVE_INSECT insect1 tectonB");
        System.out.println("2. CONSUME_SPORE insect1");
        System.out.println("3. CUT_HYPHA insect1 hypha1 tectonB");
        System.out.println("4. PLACE_FUNGUS myco1 tectonC");
        System.out.println("5. SPREAD_SPORE fungus1");
        System.out.println("6. GROW_HYPHA fungus1");
        System.out.println("7. BREAK_TECTON tectonB");
        System.out.println("8. EAT_INSECT hypha1 insect1");
        System.out.println("9. SET_FUNGUS_CHARGE fungus1 5");
        System.out.println("10. NEXT_TURN");
        System.out.println("11. NEXT_ROUND");
        System.out.println("12. SKIP_TURN");
        System.out.println("13. SELECT_ENTITY tectonA");
        System.out.println("14. SAVE test_save.json");
        System.out.println("15. LOAD test_save.json");
        System.out.println("16. END_GAME");
        System.out.println("17. AUTO_TEST (run all commands in sequence)");
        System.out.println("0. EXIT");

        boolean testing = true;
        while (testing) {
            System.out.print("\nEnter command number or full command (0 to exit): ");
            String input = inputScanner.nextLine();

            if (input.equals("0")) {
                testing = false;
                continue;
            }

            if (input.equals("17")) {
                autoTestAllCommands(game, parser, logger);
                continue;
            }

            String commandString = "";

            // Convert numbers to commands
            try {
                int option = Integer.parseInt(input);
                switch (option) {
                    case 1:
                        commandString = "MOVE_INSECT insect1 tectonB";
                        break;
                    case 2:
                        commandString = "CONSUME_SPORE insect1";
                        break;
                    case 3:
                        commandString = "CUT_HYPHA insect1 hypha1 tectonB";
                        break;
                    case 4:
                        commandString = "PLACE_FUNGUS myco1 tectonC";
                        break;
                    case 5:
                        commandString = "SPREAD_SPORE fungus1";
                        break;
                    case 6:
                        commandString = "GROW_HYPHA fungus1";
                        break;
                    case 7:
                        commandString = "BREAK_TECTON tectonB";
                        break;
                    case 8:
                        commandString = "EAT_INSECT hypha1 insect1";
                        break;
                    case 9:
                        commandString = "SET_FUNGUS_CHARGE fungus1 5";
                        break;
                    case 10:
                        commandString = "NEXT_TURN";
                        break;
                    case 11:
                        commandString = "NEXT_ROUND";
                        break;
                    case 12:
                        commandString = "SKIP_TURN";
                        break;
                    case 13:
                        commandString = "SELECT_ENTITY tectonA";
                        break;
                    case 14:
                        commandString = "SAVE test_save.json";
                        break;
                    case 15:
                        commandString = "LOAD test_save.json";
                        break;
                    case 16:
                        commandString = "END_GAME";
                        break;
                    default:
                        System.out.println("Invalid option.");
                        continue;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            {

            }

            try {
                System.out.println("Executing: " + commandString);
                Command command = parser.parse(commandString);

                boolean isValid = command.validate(game);
                System.out.println("Command validation: " + (isValid ? "VALID" : "INVALID"));

                if (isValid) {
                    command.execute(game, logger);
                    System.out.println("Command executed successfully.");

                    // Print relevant state after execution
                    printRelevantState(commandString, game, registry);
                } else {
                    System.out.println("Command validation failed. Not executed.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        inputScanner.close();
        System.out.println("Command testing completed.");
    }

    private static void autoTestAllCommands(Game game, CommandParser parser, Logger logger) {
        System.out.println("\n===== RUNNING AUTOMATIC TEST OF ALL COMMANDS =====");

        String[] commands = {
                "MOVE_INSECT insect1 tectonB",
                "SET_FUNGUS_CHARGE fungus1 5",
                "GROW_HYPHA fungus1",
                "SPREAD_SPORE fungus1",
                "MOVE_INSECT insect1 tectonA",
                "CONSUME_SPORE insect1",
                "CUT_HYPHA insect1 hypha1 tectonB",
                "PLACE_FUNGUS myco1 tectonC",
                "SELECT_ENTITY tectonA",
                "SAVE test_save.json",
                "BREAK_TECTON tectonB",
                "NEXT_TURN",
                "SKIP_TURN",
                "NEXT_ROUND",
                "LOAD test_save.json",
                "EAT_INSECT hypha1 insect1"
                // END_GAME not included in auto-test to allow further testing
        };

        for (String cmdStr : commands) {
            try {
                System.out.println("\n> Testing: " + cmdStr);
                Command command = parser.parse(cmdStr);

                boolean isValid = command.validate(game);
                System.out.println("  Validation: " + (isValid ? "VALID" : "INVALID"));

                if (isValid) {
                    command.execute(game, logger);
                    System.out.println("  Execution: SUCCESS");

                    // Print relevant state
                    printRelevantState(cmdStr, game, game.getRegistry());
                } else {
                    System.out.println("  Execution: SKIPPED (invalid command)");
                }

                // Small delay between commands for readability
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            } catch (Exception e) {
                System.out.println("  ERROR: " + e.getMessage());
            }
        }

        System.out.println("\n===== AUTO-TEST COMPLETED =====");
    }

    private static void printRelevantState(String command, Game game, EntityRegistry registry) {
        String cmdType = command.split(" ")[0];

        switch (cmdType) {
            case "MOVE_INSECT":
                String insectName = command.split(" ")[1];
                Insect insect = (Insect) registry.getByName(insectName);
                if (insect != null) {
                    Tecton tecton = insect.getTecton();
                    System.out.println("Insect location: " + registry.getNameOf(tecton));
                    System.out.println("Effects on insect: " + insect.getEffects().size());
                }
                break;

            case "CONSUME_SPORE":
                insectName = command.split(" ")[1];
                insect = (Insect) registry.getByName(insectName);
                if (insect != null) {
                    Tecton tecton = insect.getTecton();
                    System.out.println("Spores on tecton: " + tecton.spores.size());
                    System.out.println("Insect state updated");
                }
                break;

            case "GROW_HYPHA":
                String fungusName = command.split(" ")[1];
                Fungus fungus = (Fungus) registry.getByName(fungusName);
                if (fungus != null) {
                    Tecton tecton = fungus.getTecton();
                    System.out.println("Hyphae on tecton: " + tecton.hyphas.size());
                }
                break;

            case "SPREAD_SPORE":
                fungusName = command.split(" ")[1];
                fungus = (Fungus) registry.getByName(fungusName);
                if (fungus != null) {
                    System.out.println("Fungus charge: " + fungus.getCharge());
                    Tecton tecton = fungus.getTecton();
                    for (Tecton neighbor : tecton.getNeighbours()) {
                        System.out.println("Spores on neighbor: " + neighbor.spores.size());
                    }
                }
                break;

            case "BREAK_TECTON":
                System.out.println("Tectons in board: " + game.getBoard().getAllTectons().size());
                break;

            case "SET_FUNGUS_CHARGE":
                fungusName = command.split(" ")[1];
                fungus = (Fungus) registry.getByName(fungusName);
                if (fungus != null) {
                    System.out.println("Fungus charge: " + fungus.getCharge());
                }
                break;

            default:
                System.out.println("Game state updated");
                break;
        }
    }

    /**
     * GOMBÁSZ INTERATÍC teszt
     */
    public static void interactiveMycologistTest() {
        Game game = new Game();
        game.initBoard(); // véletlen board

        // 1 gombász
        Mycologist myco = new Mycologist();
        game.addPlayer(myco);

        // választható tektonok
        List<Tecton> tectons = game.getBoard().getAllTectons();
        Tecton startTecton = tectons.get(0); // egyszerűség kedvéért
        myco.debugPlaceFungus(startTecton);

        // register fungus névvel
        Fungus f = startTecton.getFungus();
        game.getRegistry().register("fungus1", f);
        game.getRegistry().register("tectonA", startTecton);
        game.getRegistry().register("myco1", myco);

        System.out.println("Gombász készen áll! Használható commandok pl:");
        System.out.println("  PLACE_FUNGUS myco1 tectonA");
        System.out.println("  GROW_HYPHA fungus1");
        System.out.println("  SPREAD_SPORE fungus1");
        System.out.println("  SET_FUNGUS_CHARGE fungus1 5");
        System.out.println("  NEXT_TURN / SKIP_TURN / END_GAME");

        Scanner scanner = new Scanner(System.in);
        CommandParser parser = new CommandParser(game);
        Logger logger = game.getLogger();

        myco.remainingActions = 5; // több akció a teszthez

        while (myco.remainingActions > 0) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line.isBlank())
                break;

            try {
                Command cmd = parser.parse(line);
                if (cmd.validate(game)) {
                    cmd.execute(game, logger);
                    myco.decreaseActions();
                } else {
                    logger.logError("CMD", cmd.toString(), "Validation failed.");
                }
            } catch (Exception e) {
                logger.logError("CMD", line, "Error: " + e.getMessage());
            }
        }

        System.out.println("Teszt vége, maradék akciók: " + myco.remainingActions);
    }

    public static void FullGameDeserializeTest() {
        try {
            System.out.println("Betöltés elindult...");

            Game loadedGame = InitLoader.loadFromFile("full_game_save.json");

            System.out.println("Betöltés sikeres!");
            System.out.println("Játékosok száma: " + loadedGame.getPlayers().size());
            System.out.println("Tectonok száma: " + loadedGame.getBoard().getAllTectons().size());

            if (loadedGame.getCurrentPlayer() != null) {
                System.out.println("Jelenlegi játékos: " + loadedGame.getCurrentPlayer().name);
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
            for (int currentRound = 1; currentRound < game.getTotalRounds() && !gameIsEnded; currentRound++) {
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

    private static void staticMap() {
        Game game = new Game(10);
        GameBoard gameBoard = game.getBoard();
        ObjectNamer namer = ObjectNamer.getInstance();

        Tecton tectonA = new InfiniteHyphaTecton();
        Tecton tectonB = new NoFungiTecton();
        Tecton tectonC = new KeepHyphaTecton();
        Tecton tectonD = new ShortHyphaTecton();
        Tecton tectonE = new SingleHyphaTecton();
        Tecton tectonF = new InfiniteHyphaTecton();
        Tecton tectonG = new SingleHyphaTecton();

        gameBoard.getAllTectons().add(tectonA);
        gameBoard.getAllTectons().add(tectonB);
        gameBoard.getAllTectons().add(tectonC);
        gameBoard.getAllTectons().add(tectonD);
        gameBoard.getAllTectons().add(tectonE);
        gameBoard.getAllTectons().add(tectonF);
        gameBoard.getAllTectons().add(tectonG);

        // Nevek hozzárendelése
        namer.register("A", tectonA);
        namer.register("B", tectonB);
        namer.register("C", tectonC);
        namer.register("D", tectonD);
        namer.register("E", tectonE);
        namer.register("F", tectonF);
        namer.register("G", tectonG);

        // Kör
        // gameBoard.connect(tectonA, tectonB);
        // gameBoard.connect(tectonA, tectonC);
        // gameBoard.connect(tectonA, tectonD);
        // gameBoard.connect(tectonA, tectonE);
        // gameBoard.connect(tectonA, tectonF);
        // gameBoard.connect(tectonA, tectonG);

        // gameBoard.connect(tectonB, tectonC);
        // gameBoard.connect(tectonB, tectonG);

        // gameBoard.connect(tectonC, tectonD);

        // gameBoard.connect(tectonD, tectonE);

        // gameBoard.connect(tectonE, tectonF);

        // gameBoard.connect(tectonF, tectonG);

        Tecton.connectTectons(tectonA, tectonB);
        Tecton.connectTectons(tectonA, tectonC);
        Tecton.connectTectons(tectonA, tectonD);
        Tecton.connectTectons(tectonA, tectonE);
        Tecton.connectTectons(tectonA, tectonF);
        Tecton.connectTectons(tectonA, tectonG);

        Tecton.connectTectons(tectonB, tectonC);
        Tecton.connectTectons(tectonB, tectonG);

        Tecton.connectTectons(tectonC, tectonD);

        Tecton.connectTectons(tectonD, tectonE);

        Tecton.connectTectons(tectonE, tectonF);

        Tecton.connectTectons(tectonF, tectonG);

        // ---
        game.initGame();
        game.startGame();

        System.out.println("DEBUG " + game.getPlayers().get(0).toString());
        System.out.println("DEBUG " + namer.getName(game.getPlayers().get(0)));
        int endOfRound = 1;
        while (endOfRound != 0) {
            endOfRound = game.nextTurn();
        }

        // After first round is complete
        if (game.getTotalRounds() > 1) {
            boolean gameIsEnded = false;
            for (int currentRound = 1; currentRound < game.getTotalRounds() && !gameIsEnded; currentRound++) {
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

    // -------------------------------- //
    public static void main(String[] args) {
        boolean menuActive = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 0 to exit\n");

        while (menuActive) {
            System.out.println("9. Serializáció teszt");
            System.out.println("10. Command test");
            System.out.println("11. Full gameplay");
            System.out.println("12. Deszerializálás");
            System.out.println("13. Tesztek futtatása");
            System.out.println("14. Full gameplay statikus mapon");
            System.out.println("15. GUI");
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
                    testAllCommands();
                    // interactiveMycologistTest();
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
                    staticMap();
                    break;
                case 15:
                    Application.launch(MainApp.class, args);
                    break;
                default:
                    System.out.println("Invalid input");
                    break;
            }
            System.out.println("");
        }
    }
}