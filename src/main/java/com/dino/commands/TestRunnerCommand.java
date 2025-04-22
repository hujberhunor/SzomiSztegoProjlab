package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.Logger;
import com.dino.util.TestOracle;

public class TestRunnerCommand implements Command {

    private final int testNumber;

    public TestRunnerCommand(int testNumber) {
        this.testNumber = testNumber;
    }

    @Override
    public void execute(Game game, Logger logger) {
        TestOracle oracle = new TestOracle();
        TestOracle.TestResult result = oracle.runTest(testNumber);

        if (result.success) {
            logger.logChange("TEST_RUNNER", "TestRunner", "RUN_TEST", "Test" + testNumber, "SUCCESS");
        } else {
            logger.logError("TEST_RUNNER", "TestRunner", "Test" + testNumber + " failed. Expected:\n" +
                    result.expectedOutput + "\nActual:\n" + result.actualOutput);
        }
    }

    /**
     * Validálja, hogy a megadott tesztszám helyes (pozitív szám).
     */
    @Override
    public boolean validate(Game game) {
        return testNumber > 0;
    }

    @Override
    public String toString() {
        return "RUN_TEST " + testNumber;
    }
}