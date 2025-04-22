package com.dino.commands;

import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.engine.Game;
import com.dino.engine.GameBoard;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class CutHyphaCommand implements Command {

    private final String insectName;
    private final String hyphaName;
    private final String targetTectonName;

    public CutHyphaCommand(String insectName, String hyphaName, String targetTectonName) {
        this.insectName = insectName;
        this.hyphaName = hyphaName;
        this.targetTectonName = targetTectonName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        GameBoard board = game.getBoard();

        Insect insect = (Insect) registry.getByName(insectName);
        Hypha hypha = (Hypha) registry.getByName(hyphaName);
        Tecton targetTecton = (Tecton) registry.getByName(targetTectonName);
        String insectId = registry.getNameOf(insect);

        boolean success = insect.cutHypha(hypha, targetTecton);
        if (success) {
            logger.logChange("INSECT", insect, "CUT_HYPHA", hyphaName + ":" + targetTectonName, "SUCCESS");
        } else {
            logger.logError("INSECT", insectId, "Failed to cut hypha at target tecton.");
        }
    }

    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        return reg.getByName(insectName) instanceof Insect
            && reg.getByName(hyphaName) instanceof Hypha
            && reg.getByName(targetTectonName) instanceof Tecton;
    }

    @Override
    public String toString() {
        return "CUT_HYPHA " + insectName + " " + hyphaName + " " + targetTectonName;
    }
}
