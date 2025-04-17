package com.dino.commands;

import com.dino.core.Insect;
import com.dino.engine.Game;
import com.dino.engine.GameBoard;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class MoveInsectCommand implements Command {

    private final String insectName;
    private final String tectonName;

    public MoveInsectCommand(String insectName, String tectonName) {
        this.insectName = insectName;
        this.tectonName = tectonName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        GameBoard board = game.getBoard();

        Insect insect = (Insect) registry.getByName(insectName);
        Tecton current = insect.getTecton();
        Tecton target = (Tecton) registry.getByName(tectonName);

        String insectId = registry.getNameOf(insect);

        /// Szomszéd-e a target tecton?
        if (!board.getNeighbors(current).contains(target)) {
            logger.logError("INSECT", insectId, "Target Tecton is not a neighbor.");
            return;
        }

        /// Van-e fonál curr és a target között?
        /// TODO Levi branche
        if (!current.hasHypha(target)) {
            logger.logError("INSECT", insectId, "No hypha between current and target Tecton.");
            return;
        }

        // Bénult-e?
        boolean isParalyzed = insect.getEffects().stream()
                .anyMatch(e -> e.getClass().getSimpleName().equals("ParalyzingEffect"));
        if (isParalyzed) {
            logger.logError("INSECT", insectId, "Insect is paralyzed.");
            return;
        }

        // Mozgás + logolás
        String prevTecton = registry.getNameOf(current);
        insect.move(target);
        String newTecton = registry.getNameOf(target);

        logger.logChange("INSECT", insectId, "POSITION", prevTecton, newTecton);
    }

    /**
     * Feladata, hogy validjálja, hogy a user valid, helyes commandot adott meg
     * Ez a paraméterek lézezését tesztelni
     */
    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        Insect insect = (Insect) reg.getByName(insectName);
        Tecton target = (Tecton) reg.getByName(tectonName);

        if (insect == null || target == null)
            return false;
        Tecton current = insect.getTecton();

        return current.isNeighbor(target) && current.hasHypha(target);
    }

    @Override
    public String toString() {
        return "MOVE_INSECT " + insectName + " " + tectonName;
    }
}