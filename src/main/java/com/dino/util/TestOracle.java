import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.dino.commands.Command;
import com.dino.commands.CommandParser;
import com.dino.engine.Game;

public class TestOracle {
    private final String testFolderPath = "tests/";

    public TestResult runTest(int testNumber) {
        String testPath = testFolderPath + "test" + testNumber + "/";

        String initJson = loadFile(testPath + "init.json");
        String inputCommands = loadFile(testPath + "input.txt");
        String expectedOutput = loadFile(testPath + "expected_output.txt");

        Game game = setupGame(initJson);
        String actualOutput = executeCommands(game, inputCommands);

        boolean success = actualOutput.equals(expectedOutput);

        return new TestResult(testNumber, success, actualOutput, expectedOutput);
    }

    private String loadFile(String filePath) {
        try {
            return Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Hiba a fájl betöltésekor: " + filePath, e);
        }
    }

    private Game setupGame(String initJson) {
        // Game inicializálása a JSON-ból (már meglévő szerializációs mechanizmusod alapján)
        Game game = new Game();
        game.loadFromJson(initJson);
        return game;
    }

    private String executeCommands(Game game, String commands) {
        // Bemeneti parancsok feldolgozása és futtatása
        StringBuilder output = new StringBuilder();
        Scanner scanner = new Scanner(commands);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Command command = CommandParser.parse(line, game);
            String result = command.execute();
            output.append(result).append("\n");
        }
        scanner.close();
        return output.toString().trim();
    }

    public class TestResult {
        public final int testNumber;
        public final boolean success;
        public final String actualOutput;
        public final String expectedOutput;

        public TestResult(int testNumber, boolean success, String actualOutput, String expectedOutput) {
            this.testNumber = testNumber;
            this.success = success;
            this.actualOutput = actualOutput;
            this.expectedOutput = expectedOutput;
        }
    }
}
