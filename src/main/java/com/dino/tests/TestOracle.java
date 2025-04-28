package com.dino.tests;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.engine.Game;
import com.dino.util.InitLoader;
import com.dino.util.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TestOracle {

    public static boolean runTest(int testNumber) {
        try {
            // 1. Játékállapot betöltése
            Game game = InitLoader.loadFromFile(
                "src/main/java/com/dino/tests/test" +
                testNumber +
                "/test" +
                testNumber +
                ".json"
            );

            // 2. Logger inicializálása
            Logger logger = Logger.getInstance();

            // 3. CommandParser inicializálása
            CommandParser parser = new CommandParser(game);

            // 4. Bemeneti parancsok betöltése
            List<String> commandLines = Files.readAllLines(
                Path.of(
                    "src/main/java/com/dino/tests/test" + testNumber + "/be.txt"
                )
            );
            for (String line : commandLines) {
                if (line.isBlank()) continue;
                Command command = parser.parse(line);
                command.execute(game, logger);
            }

            // 5. Elvárt kimenet beolvasása
            String expectedOutput = Files.readString(
                Path.of(
                    "src/main/java/com/dino/tests/test" + testNumber + "/ki.txt"
                )
            );

            // 6. Tényleges kimenet lekérése
            String actualOutput = logger.getLog();

            // 7. Összehasonlítás
            String normalizedExpected = normalize(expectedOutput);
            String normalizedActual = normalize(actualOutput);

            // Call the debug comparison
            debugComparison(normalizedExpected, normalizedActual);

            if (normalizedExpected.equals(normalizedActual)) {
                return true; // Passed
            } else {
                // Hibás eset: actual log mentése file-ba
                Path outputPath = Path.of(
                    "output/test" + testNumber + "-actual.log"
                );
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

    private static void debugComparison(String expected, String actual) {
        System.out.println(
            "=== Expected length: " + expected.length() + " ==="
        );
        System.out.println("=== Actual length: " + actual.length() + " ===");

        // Compare character by character
        int minLength = Math.min(expected.length(), actual.length());
        boolean foundDifference = false;
        for (int i = 0; i < minLength; i++) {
            if (expected.charAt(i) != actual.charAt(i)) {
                System.out.println("Difference at position " + i + ":");
                int start = Math.max(0, i - 10);
                int end = Math.min(minLength, i + 10);
                System.out.println(
                    "Expected: " + expected.substring(start, end)
                );
                System.out.println("Actual:   " + actual.substring(start, end));
                System.out.println(
                    "Character codes - Expected: " +
                    (int) expected.charAt(i) +
                    ", Actual: " +
                    (int) actual.charAt(i)
                );
                foundDifference = true;
                break;
            }
        }

        // If strings are different lengths but the common part matches
        if (!foundDifference && expected.length() != actual.length()) {
            System.out.println(
                "Strings have different lengths, but common part matches."
            );
            if (expected.length() > actual.length()) {
                System.out.println(
                    "Expected has " +
                    (expected.length() - actual.length()) +
                    " more characters at the end."
                );
                System.out.println(
                    "Extra characters in expected: " +
                    expected.substring(actual.length())
                );
            } else {
                System.out.println(
                    "Actual has " +
                    (actual.length() - expected.length()) +
                    " more characters at the end."
                );
                System.out.println(
                    "Extra characters in actual: " +
                    actual.substring(expected.length())
                );
            }
        }
    }
}
