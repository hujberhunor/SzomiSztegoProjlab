package com.dino.tests;

import java.util.List;

public class TestRunner {

    public static void runTests(List<Integer> tests) {
        int passed = 0;

        for (int test : tests) {
            boolean ok = TestOracle.runTest(test);
            System.out.println(
                "Test " + test + ": " + (ok ? "PASSED" : "FAILED")
            );
            if (ok) passed++;
        }

        System.out.println(
            "Summary: " + passed + " / " + tests.size() + " tests passed."
        );
    }

    public static void runAllTests() {
        // Itt állíthatod be, hogy melyik tesztszámokat futtassa
        runTests(List.of(2, 3, 6, 7));
        // runTests(List.of(0)); // csak teszt0 most
    }

    public static void runSingleTest(int testNumber) {
        runTests(List.of(testNumber));
    }
}
