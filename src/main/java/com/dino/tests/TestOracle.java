package com.dino.tests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.engine.Game;
import com.dino.util.InitLoader;
import com.dino.util.Logger;

public class TestOracle {

    public static boolean runTest(int testNumber) {
        try {
            // 1. Játékállapot betöltése
            Game game = InitLoader.loadFromFile("src/main/java/com/dino/tests/test" + testNumber + "/test" + testNumber + ".json");

            // 2. Logger inicializálása
            Logger logger = Logger.getInstance();

            // 3. CommandParser inicializálása
            CommandParser parser = new CommandParser(game);

            // 4. Bemeneti parancsok betöltése
            List<String> commandLines = Files.readAllLines(Path.of("src/main/java/com/dino/tests/test" + testNumber + "/be.txt"));
            for (String line : commandLines) {
                if (line.isBlank()) continue;
                Command command = parser.parse(line);
                command.execute(game, logger);
            }

            // 5. Elvárt kimenet beolvasása
            String expectedOutput = Files.readString(Path.of("src/main/java/com/dino/tests/test" + testNumber + "/ki.txt"));

            // 6. Tényleges kimenet lekérése
            String actualOutput = logger.getLog();

            // 7. Összehasonlítás
            if (normalize(expectedOutput).equals(normalize(actualOutput))) {
                return true; // Passed
            } else {
                // Hibás eset: actual log mentése file-ba
                Path outputPath = Path.of("output/test" + testNumber + "-actual.log");
                Files.createDirectories(outputPath.getParent()); // Biztosítsuk hogy az output/ létezik
                logger.saveLogToFile(outputPath.toString());
                return false; // Failed
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Exception esetén is fail
        }
    }

    private static String normalize(String s) {
        return s.trim().replace("\r\n", "\n").replace("\r", "\n");
    }
}
