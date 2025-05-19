package com.dino.commands;

import java.util.Arrays;
import java.util.List;

import com.dino.engine.Game;
import com.dino.util.Logger;

public class HelpCommand implements Command {

    @Override
    public void execute(Game game, Logger logger) {
        List<String> commands = Arrays.asList(
                "Non case sensitive",
                "INIT <filename>",
                "LOAD <filename>",
                "SAVE <filename>",
                "MOVE_INSECT <insect> <tecton>",
                "CONSUME_SPORE <insect>",
                "GROW_HYPHA <fungus> <tecton>",
                "CONTINUE_HYPHA <hypha> <tecton>",
                "CUT_HYPHA <insect> <hypha> <neighbourTecton>",
                "PLACE_FUNGUS <mycologist> <tecton>",
                "SPREAD_SPORE <fungus>",
                "BREAK_TECTON <tecton>",
                "EAT_INSECT <hypha> <insect>",
                "SET_FUNGUS_CHARGE <fungus> <charge>",
                "NEXT_TURN", "NEXT_ROUND", "END_GAME", "SKIP_TURN",
                "SELECT_ENTITY <name>", "HELP");

        logger.logChange("HELP", game, "COMMAND_LIST", "-", String.join(" \n ", commands));
    }

    @Override
    public boolean validate(Game game) {
        return true;
    }

    @Override
    public String toString() {
        return "HELP";
    }
}
