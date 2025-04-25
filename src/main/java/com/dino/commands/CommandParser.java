package com.dino.commands;

import com.dino.engine.Game;

public class CommandParser {

    private final Game game;

    public CommandParser(Game game) {
        this.game = game;
    }

    public Command parse(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0) throw new IllegalArgumentException("Empty command");

        String commandType = parts[0].toUpperCase();

        switch (commandType) {
            case "MOVE_INSECT":
                if (parts.length != 3) throw new IllegalArgumentException("MOVE_INSECT needs 2 arguments");
                return new MoveInsectCommand(parts[1], parts[2]);
            case "NEXT_TURN":
                return new NextTurnCommand();
            case "SKIP_TURN":
                return new SkipTurnCommand();
            case "NEXT_ROUND":
                return new NextRoundCommand();

            // case "CONSUME_SPORE":
            //     if (parts.length != 2) throw new IllegalArgumentException("CONSUME_SPORE needs 1 argument");
            //     return new ConsumeSporeCommand(parts[1], game);

            // case "APPLY_EFFECT":
            //     if (parts.length != 3) throw new IllegalArgumentException("APPLY_EFFECT needs 2 arguments");
            //     return new ApplyEffectCommand(parts[1], parts[2], game);

            // case "CUT_HYPHA":
            //     if (parts.length != 3) throw new IllegalArgumentException("CUT_HYPHA needs 2 arguments");
            //     return new CutHyphaCommand(parts[1], parts[2], game);

            // case "CLONE_INSECT":
            //     if (parts.length != 2) throw new IllegalArgumentException("CLONE_INSECT needs 1 argument");
            //     return new CloneInsectCommand(parts[1], game);

            // case "END_TURN":
            //     return new EndTurnCommand(game);

            default:
                throw new IllegalArgumentException("Unknown command: " + commandType);
        }
    }
}
