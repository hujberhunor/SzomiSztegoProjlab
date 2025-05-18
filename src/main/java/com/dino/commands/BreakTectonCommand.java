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
        try {
            EntityRegistry registry = game.getRegistry();
            Tecton original = (Tecton) registry.getByName(tectonName);
            String originalName = registry.getNameOf(original);

            if (original == null) {
                logger.logError("TECTON", tectonName, "Tecton nem található");
                return;
            }

            // 1. Először csak modell-műveleteket végzünk
            // Az eredeti Tectont eltávolítjuk a játéktábláról
            game.getBoard().getAllTectons().remove(original);

            // Törés végrehajtása
            List<Tecton> newTectons = original.split(-1);

            if (newTectons.isEmpty()) {
                logger.logError("TECTON", originalName, "Tecton nem törhet el");
                // Visszatesszük az eredeti tectont
                game.getBoard().getAllTectons().add(original);
                return;
            }

            // Elnevezés és regisztráció
            String baseName = originalName;
            String newNameA = baseName + "_A";
            String newNameB = baseName + "_B";

            // Elkerüljük a duplikált neveket
            int counter = 1;
            while (registry.isNameRegistered(newNameA)) {
                newNameA = baseName + "_A" + counter;
                counter++;
            }
            counter = 1;
            while (registry.isNameRegistered(newNameB)) {
                newNameB = baseName + "_B" + counter;
                counter++;
            }

            registry.register(newNameA, newTectons.get(0));
            registry.register(newNameB, newTectons.get(1));

            // Az új tektonokat hozzáadjuk a játéktáblához
            game.getBoard().getAllTectons().add(newTectons.get(0));
            game.getBoard().getAllTectons().add(newTectons.get(1));

            logger.logChange("TECTON", original, "BREAK", baseName, newNameA + ", " + newNameB);

            // 2. Végül értesítjük az observereket, hogy frissítsék a nézetüket
            // Ez biztonságos a főszálon is
            game.notifyObservers();

        } catch (Exception e) {
            logger.logError("BREAK_TECTON", tectonName, "Hiba történt: " + e.getMessage());
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
