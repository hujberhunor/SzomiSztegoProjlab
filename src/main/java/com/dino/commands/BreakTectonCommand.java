package com.dino.commands;

import java.util.List;
import java.util.stream.Collectors;

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

        // Új tekton az ős + A vagy B nevet kapja
        if (registry.getByName(newNameA) != null) {
            newNameA += "_" + System.currentTimeMillis();
        }
        if (registry.getByName(newNameB) != null) {
            newNameB += "_" + System.currentTimeMillis();
        }

        // Új tektonok regisztrálása és logolása
        registry.register(newNameA, newTectons.get(0));
        registry.register(newNameB, newTectons.get(1));

        // Javítás 3: Az új tektonokat hozzá kell adni a játéktáblához
        game.getBoard().getAllTectons().add(newTectons.get(0));
        game.getBoard().getAllTectons().add(newTectons.get(1));

        logger.logChange("TECTON", original, "BREAK", baseName, newNameA);
        logger.logChange("TECTON", original, "BREAK", baseName, newNameB);

        // Új tektonok újraszínezése a GUI-n
        for (ModelObserver observer : game.getObservers()) {
            if (observer instanceof GuiBoard) {
                GuiBoard guiBoard = (GuiBoard) observer;
                guiBoard.recolorTecton(newTectons.get(0), newTectons.get(1));

                // Javítás 4: Teljes újrarajzolás erőltetése
                guiBoard.boardPane.getChildren().clear();
                guiBoard.render(game);
                break;
            }
        }

        game.notifyObservers();
        // A command végére:
        // Debug információ kiírása
        System.out.println("DEBUG: Original tecton hexagons: " +
                original.hexagons.stream().map(h -> Integer.toString(h.getId())).collect(Collectors.joining(",")));

        System.out.println("DEBUG: New tecton count: " + newTectons.size());
        for (int i = 0; i < newTectons.size(); i++) {
            System.out.println("DEBUG: New tecton " + i + " hexagons: " +
                    newTectons.get(i).hexagons.stream().map(h -> Integer.toString(h.getId()))
                            .collect(Collectors.joining(",")));
        }

        System.out.println("DEBUG: Game board tecton count: " + game.getBoard().getAllTectons().size());
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
