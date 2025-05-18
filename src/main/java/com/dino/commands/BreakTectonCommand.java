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

        try {
            // Az eredeti Tectont eltávolítjuk a játéktábláról
            game.getBoard().getAllTectons().remove(original);

            // Törés végrehajtása
            List<Tecton> newTectons = original.split(-1);

            if (newTectons.isEmpty()) {
                logger.logError("TECTON", originalName, "Tecton cannot break.");
                game.getBoard().getAllTectons().add(original);
                return;
            }

            // Elnevezés és regisztráció
            String baseName = originalName;
            String newNameA = baseName + "_A";
            String newNameB = baseName + "_B";

            if (registry.isNameRegistered(newNameA)) {
                newNameA = baseName + "_A_" + System.currentTimeMillis();
            }
            if (registry.isNameRegistered(newNameB)) {
                newNameB = baseName + "_B_" + System.currentTimeMillis();
            }

            registry.register(newNameA, newTectons.get(0));
            registry.register(newNameB, newTectons.get(1));

            // Az új tektonokat hozzáadjuk a játéktáblához
            game.getBoard().getAllTectons().add(newTectons.get(0));
            game.getBoard().getAllTectons().add(newTectons.get(1));

            logger.logChange("TECTON", original, "BREAK", baseName, newNameA + ", " + newNameB);

            // GUI frissítése JavaFX alkalmazás szálán, de csak célzottan
            javafx.application.Platform.runLater(() -> {
                try {
                    // Csak a színeket frissítjük, nem rajzolunk újra mindent
                    for (ModelObserver observer : game.getObservers()) {
                        if (observer instanceof GuiBoard) {
                            GuiBoard guiBoard = (GuiBoard) observer;
                            guiBoard.recolorTecton(newTectons.get(0), newTectons.get(1));
                            break;
                        }
                    }

                    // Értesítjük az összes megfigyelőt a változásról
                    game.notifyObservers();
                } catch (Exception e) {
                    System.err.println("GUI frissítési hiba: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            logger.logError("BREAK_TECTON", originalName, "Hiba történt: " + e.getMessage());
            e.printStackTrace();
        }
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
