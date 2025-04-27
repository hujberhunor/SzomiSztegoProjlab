package com.dino.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TestRunner {
    
    /**
     * Megadott tesztszámok futtatása
     */
    public static void runTests(List<Integer> tests) {
        int passed = 0;

        for (int test : tests) {
            boolean ok = TestOracle.runTest(test);
            System.out.println("Teszt " + test + ": " + (ok ? "SIKERES" : "SIKERTELEN"));
            if (ok) passed++;
        }

        System.out.println("Összegzés: " + passed + " / " + tests.size() + " teszt sikeres.");
    }
    
    /**
     * Az összes elérhető teszt felderítése és futtatása
     */
    public static void runAllTests() {
        List<Integer> availableTests = detectAvailableTests();
        
        if (availableTests.isEmpty()) {
            System.out.println("Nem található teszt könyvtár.");
            return;
        }
        
        System.out.println(availableTests.size() + " teszt található: " + availableTests);
        runTests(availableTests);
    }
    
    /**
     * Egy konkrét teszt futtatása szám alapján
     */
    public static void runSingleTest(int testNumber) {
        runTests(List.of(testNumber));
    }
    
    /**
     * Interaktív menü megjelenítése a teszt kiválasztásához
     */
    public static void showTestMenu() {
        Scanner scanner = new Scanner(System.in);
        List<Integer> availableTests = detectAvailableTests();
        
        System.out.println("==========================================");
        System.out.println("           TESZT FUTTATÓ MENÜ            ");
        System.out.println("==========================================");
        System.out.println("1. Összes felderített teszt futtatása");
        System.out.println("2. Egy konkrét teszt futtatása");
        System.out.println("0. Kilépés");
        System.out.print("Válassz: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Új sor elfogyasztása
        
        switch (choice) {
            case 0:
                System.out.println("Kilépés a teszt futtatóból.");
                break;
                
            case 1:
                if (availableTests.isEmpty()) {
                    System.out.println("Nem található teszt könyvtár!");
                } else {
                    System.out.println("Összes felderített teszt futtatása (" + availableTests.size() + " db)...");
                    runTests(availableTests);
                }
                break;
                
            case 2:
                if (availableTests.isEmpty()) {
                    System.out.println("Nem található teszt könyvtár!");
                } else {
                    System.out.println("Elérhető tesztek: " + availableTests);
                    System.out.print("Add meg a futtatandó teszt számát: ");
                    int testNum = scanner.nextInt();
                    scanner.nextLine(); // Új sor elfogyasztása
                    
                    if (availableTests.contains(testNum)) {
                        runSingleTest(testNum);
                    } else {
                        System.out.println("Érvénytelen tesztszám. 'test" + testNum + "' könyvtár nem található.");
                    }
                }
                break;
                
            default:
                System.out.println("Érvénytelen választás.");
                break;
        }
    }
    
    /**
     * Az összes elérhető teszt könyvtár felderítése
     */
    private static List<Integer> detectAvailableTests() {
        List<Integer> testNumbers = new ArrayList<>();
        File testsDir = new File("src/main/java/com/dino/tests");
        
        if (!testsDir.exists() || !testsDir.isDirectory()) {
            System.err.println("Tesztek könyvtár nem található: " + testsDir.getAbsolutePath());
            return testNumbers;
        }
        
        File[] files = testsDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().startsWith("test")) {
                    try {
                        int testNum = Integer.parseInt(file.getName().substring(4));
                        // Szükséges fájlok meglétének ellenőrzése
                        File jsonFile = new File(file, "test" + testNum + ".json");
                        File inputFile = new File(file, "be.txt");
                        File outputFile = new File(file, "ki.txt");
                        
                        if (jsonFile.exists() && inputFile.exists() && outputFile.exists()) {
                            testNumbers.add(testNum);
                        }
                    } catch (NumberFormatException e) {
                        // Nem érvényes teszt könyvtár
                    }
                }
            }
        }
        
        // Tesztszámok rendezése
        testNumbers.sort(null);
        return testNumbers;
    }
}