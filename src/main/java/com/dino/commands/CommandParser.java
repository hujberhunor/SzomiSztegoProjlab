package com.dino.commands;

import com.dino.engine.Game;

public class CommandParser {

    private final Game game;

    public CommandParser(Game game) {
        this.game = game;
    }

    public Command parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Empty command line.");
        }

        String[] parts = line.trim().split("\\s+");
        String commandType = parts[0].toUpperCase();

        switch (commandType) {
            case "INIT":
                return new InitCommand(parts[1]);
            case "LOAD":
                return new LoadCommand(parts[1]);
            case "SAVE":
                return new SaveCommand(parts[1]);
            case "MOVE_INSECT":
                return new MoveInsectCommand(parts[1], parts[2]);
            case "CONSUME_SPORE", "EAT_SPORE":
                return new ConsumeSporeCommand(parts[1]);
            case "GROW_HYPHA":
                return new GrowHyphaCommand(parts[1], parts[2]);
            case "CONTINUE_HYPHA":
                return new ContinueHyphaCommand(parts[1], parts[2]);
            case "CUT_HYPHA":
                return new CutHyphaCommand(parts[1], parts[2], parts[3]);
            case "PLACE_FUNGUS":
                return new PlaceFungusCommand(parts[1], parts[2]);
            case "SPREAD_SPORE":
                return new SpreadSporeCommand(parts[1]);
            case "BREAK_TECTON":
                return new BreakTectonCommand(parts[1]);
            case "EAT_INSECT":
                return new EatInsectCommand(parts[1], parts[2]);
            case "SET_FUNGUS_CHARGE":
                return new SetFungusChargeCommand( parts[1], Integer.parseInt(parts[2]));
            case "NEXT_TURN", "SKIP_TURN":
                return new NextTurnCommand();
            case "NEXT_ROUND", "SKIP_ROUND":
                return new NextRoundCommand();
            case "END_GAME":
                return new EndGameCommand();
            case "SELECT_ENTITY":
                return new SelectEntityCommand(parts[1]);
            case "HELP":
                return new HelpCommand();
            default:
                throw new IllegalArgumentException(
                    "Unknown command: " + commandType
                );
        }
    }
}
