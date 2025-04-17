package com.dino.commands;

import com.dino.core.Insect;
import com.dino.engine.Game;
import com.dino.engine.GameBoard;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;

public class MoveInsectCommand implements Command {

    private final String insectName;
    private final String targetTectonName;

    public MoveInsectCommand(String insectName, String targetTectonName) {
        this.insectName = insectName;
        this.targetTectonName = targetTectonName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getEntityRegistry();
        GameBoard board = game.getGameBoard();

        Insect insect = (Insect) registry.get(insectName);
        Tecton current = insect.getTecton();
        Tecton target = (Tecton) registry.get(targetTectonName);

        if (!board.areConnected(current, target)) {
            logger.logError("INSECT", insect, "Target tecton is not a neighbor.");
            return;
        }

        if (!board.hasHypha(current, target)) {
            logger.logError("INSECT", insect, "No hypha between tectons.");
            return;
        }

        if (insect.isParalyzed()) {
            logger.logError("INSECT", insect, "Insect is paralyzed.");
            return;
        }

        String prevTecton = registry.getNameOf(current);
        insect.move(target);
        String newTecton = registry.getNameOf(target);

        logger.logChange("INSECT", insect, "POSITION", prevTecton, newTecton);
    }

    @Override
    public String toString() {
        return "MOVE_INSECT " + insectName + " " + targetTectonName;
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public String Serialize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Serialize'");
    }
}
