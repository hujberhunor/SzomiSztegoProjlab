package com.dino.tests;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.engine.Game;
import com.dino.util.InitLoader;
import com.dino.util.Logger;

public class TestOracle {

    public static boolean runTest(int testNumber) {
        try {
            System.out.println("Teszt indítása: " + testNumber + "...");
            
            // Teszt könyvtár meghatározása
            String baseDir = "src/main/java/com/dino/tests/test" + testNumber;
            File directory = new File(baseDir);
            if (!directory.exists() || !directory.isDirectory()) {
                System.err.println("Teszt könyvtár nem található: " + baseDir);
                return false;
            }
            
            // JSON fájl keresése a könyvtárban (test{szám}.json formátumban)
            String jsonFileName = baseDir + "/test" + testNumber + ".json";
            File jsonFile = new File(jsonFileName);
            if (!jsonFile.exists()) {
                System.err.println("Teszt JSON fájl nem található: " + jsonFileName);
                return false;
            }
            
            System.out.println("Játékállapot betöltése innen: " + jsonFileName);
            
            // 1. Játékállapot betöltése
            Game game = InitLoader.loadFromFile(jsonFileName);
            if (game == null) {
                System.err.println("Játékállapot betöltése sikertelen");
                return false;
            }
            
            // 2. Logger inicializálása
            Logger logger = new Logger(game.getRegistry());
            
            // 3. CommandParser inicializálása
            CommandParser parser = new CommandParser(game);
            
            // 4. Bemeneti parancsok betöltése
            Path inputPath = Paths.get(baseDir, "be.txt");
            if (!Files.exists(inputPath)) {
                System.err.println("Bemeneti fájl nem található: " + inputPath);
                return false;
            }
            
            System.out.println("Parancsok végrehajtása innen: " + inputPath);
            List<String> commandLines = Files.readAllLines(inputPath);
            
            // Parancsok végrehajtása
            for (String line : commandLines) {
                if (line.isBlank()) continue;
                System.out.println("Parancs végrehajtása: " + line);
                try {
                    Command command = parser.parse(line);
                    command.execute(game, logger);
                } catch (Exception e) {
                    System.err.println("Hiba a parancs végrehajtása közben '" + line + "': " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // 5. Elvárt kimenet betöltése
            Path expectedOutputPath = Paths.get(baseDir, "ki.txt");
            if (!Files.exists(expectedOutputPath)) {
                System.err.println("Elvárt kimenet fájl nem található: " + expectedOutputPath);
                return false;
            }
            
            String expectedOutput = Files.readString(expectedOutputPath);
            
            // 6. Tényleges kimenet lekérése
            String actualOutput = logger.getLog();
            
            // 7. Összehasonlítás
            if (compareOutputs(normalize(expectedOutput), normalize(actualOutput))) {
                System.out.println("Teszt " + testNumber + " SIKERES");
                return true;
            } else {
                System.out.println("Teszt " + testNumber + " SIKERTELEN");
                
                // Tényleges kimenet mentése hibakereséshez
                Path outputDir = Paths.get("output");
                Files.createDirectories(outputDir);
                Path actualOutputPath = outputDir.resolve("test" + testNumber + "-actual.log");
                Files.writeString(actualOutputPath, actualOutput);
                
                System.out.println("Tényleges kimenet mentve ide: " + actualOutputPath);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Hiba a teszt futtatása közben " + testNumber + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean compareOutputs(String expected, String actual) {
        if (expected.equals(actual)) {
            return true;
        }
        
        // Különbségek naplózása hibakereséshez
        System.out.println("Kimenet összehasonlítás sikertelen!");
        System.out.println("Elvárt hossz: " + expected.length() + ", Tényleges hossz: " + actual.length());
        
        // Első különbség keresése
        int minLength = Math.min(expected.length(), actual.length());
        for (int i = 0; i < minLength; i++) {
            if (expected.charAt(i) != actual.charAt(i)) {
                int contextStart = Math.max(0, i - 20);
                int contextEnd = Math.min(minLength, i + 20);
                
                System.out.println("Első különbség a " + i + ". pozícióban:");
                System.out.println("Elvárt:   ..." + expected.substring(contextStart, contextEnd) + "...");
                System.out.println("Tényleges: ..." + actual.substring(contextStart, contextEnd) + "...");
                break;
            }
        }
        
        return false;
    }

    private static String normalize(String s) {
        return s.trim().replace("\r\n", "\n").replace("\r", "\n");
    }
}