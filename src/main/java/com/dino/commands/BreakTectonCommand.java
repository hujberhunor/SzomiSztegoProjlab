package com.dino.commands;

import java.util.List;
import java.util.stream.Collectors;

import com.dino.core.Hexagon;
import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.view.GuiBoard;
import com.dino.view.ModelObserver;

public class BreakTectonCommand implements Command {

    private final String tectonName;

    public BreakTectonCommand(String tectonName) {
        this.tectonName = tectonName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        Tecton original = (Tecton) registry.getByName(tectonName);
        String originalName = registry.getNameOf(original);

        // Javítás 1: Az eredeti Tectont el kell távolítani a játéktábláról
        game.getBoard().getAllTectons().remove(original);

        List<Tecton> newTectons = original.split(-1);

        if (newTectons.isEmpty()) {
            logger.logError("TECTON", originalName, "Tecton cannot break: Not because of breakchance.");
            // Javítás 2: Ha nem sikerült a törés, vissza kell tenni az eredeti Tectont
            game.getBoard().getAllTectons().add(original);
            return;
        }

        // Új tektonok regisztrálása és logolása
        String baseName = registry.getNameOf(original);
        String newNameA = baseName + "_A";
        String newNameB = baseName + "_B";

        if (registry.getByName(newNameA) != null) {
            newNameA += "_" + System.currentTimeMillis();
        }
        if (registry.getByName(newNameB) != null) {
            newNameB += "_" + System.currentTimeMillis();
        }

        registry.register(newNameA, newTectons.get(0));
        registry.register(newNameB, newTectons.get(1));

        // Javítás 3: Az új tektonokat hozzá kell adni a játéktáblához
        game.getBoard().getAllTectons().add(newTectons.get(0));
        game.getBoard().getAllTectons().add(newTectons.get(1));

        // Javítás 4: Szomszédsági kapcsolatok beállítása az új Tectonok között
        Tecton.connectTectons(newTectons.get(0), newTectons.get(1));
        logger.logChange("TECTON", newTectons.get(0), "NEIGHBOURS_ADD", "-", newNameB);

        // Javítás 5: Szomszédsági kapcsolatok átvitele az eredeti Tectonról
        for (Tecton neighbour : original.getNeighbours()) {
            // Csak akkor ha nem az eredeti Tecton (ami már törölve van)
            if (!neighbour.equals(original)) {
                for (Tecton newTecton : newTectons) {
                    if (areTectonsNeighbours(newTecton, neighbour)) {
                        Tecton.connectTectons(newTecton, neighbour);
                        logger.logChange("TECTON", newTecton, "NEIGHBOURS_ADD", "-",
                                registry.getNameOf(neighbour));
                    }
                }
            }
        }

        logger.logChange("TECTON", original, "BREAK", baseName, newNameA);
        logger.logChange("TECTON", original, "BREAK", baseName, newNameB);

        // GUI frissítése
        for (ModelObserver observer : game.getObservers()) {
            if (observer instanceof GuiBoard) {
                GuiBoard guiBoard = (GuiBoard) observer;
                guiBoard.recolorTecton(newTectons.get(0), newTectons.get(1));
            }
        }

        game.notifyObservers();
    }

    // Segédmetódus, ami meghatározza, hogy két Tecton szomszédos-e a hexagonok alapján
    private boolean areTectonsNeighbours(Tecton a, Tecton b) {
        for (Hexagon hexA : a.getHexagons()) {
            for (Hexagon hexB : b.getHexagons()) {
                if (hexA.getNeighbours().contains(hexB)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validálja, hogy a megadott Tecton objektum létezik
     */
    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        return reg.getByName(tectonName) instanceof Tecton;
    }

    @Override
    public String toString() {
        return "BREAK " + tectonName;
    }
}
