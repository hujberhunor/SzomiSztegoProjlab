package com.dino.commands;

import com.dino.engine.Game;

public class CommandParser {

    private final Game game;

    public CommandParser(Game game) {
        this.game = game;
    }

    public Command parse(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0) throw new IllegalArgumentException(
            "Empty command"
        );

        String commandType = parts[0].toUpperCase();

        switch (commandType) {
            case "MOVE_INSECT":
                if (parts.length != 3) throw new IllegalArgumentException(
                    "MOVE_INSECT needs 2 arguments"
                );
                return new MoveInsectCommand(parts[1], parts[2]);
            case "NEXT_TURN":
                return new NextTurnCommand();
            case "SKIP_TURN":
                return new SkipTurnCommand();
            case "NEXT_ROUND":
                return new NextRoundCommand();
            case "INIT":
                if (parts.length != 2) throw new IllegalArgumentException(
                    "INIT needs 1 argument (filename)"
                );
                return new InitCommand(parts[1]);
            case "SAVE":
                if (parts.length != 2) throw new IllegalArgumentException(
                    "SAVE needs 1 argument (filename)"
                );
                return new SaveCommand(parts[1]);
            case "LOAD":
                if (parts.length != 2) throw new IllegalArgumentException(
                    "LOAD needs 1 argument (filename)"
                );
                return new LoadCommand(parts[1]);
            // In the switch statement in CommandParser.java
            case "CUT_HYPHA":
                if (parts.length != 4) throw new IllegalArgumentException(
                    "CUT_HYPHA needs 3 arguments"
                );
                return new CutHyphaCommand(parts[1], parts[2], parts[3]);
            // TODO  folytatni
            default:
                throw new IllegalArgumentException(
                    "Unknown command: " + commandType
                );
        }
    }
}
