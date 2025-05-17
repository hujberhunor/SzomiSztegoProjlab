package com.dino.commands;

import java.util.List;

import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

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

        // Tekton törése
        List<Tecton> newTectons = original.split(original.getBreakChance());

        if (newTectons.isEmpty()) {
            logger.logError("TECTON", originalName, "Tecton did not break.");
            return;
        }



        // Új tektonok regisztrálása és logolása
        for (Tecton newTecton : newTectons) {
            String baseName = registry.getNameOf(original);
            String newNameA = baseName + "_A";
            String newNameB = baseName + "_B";
           
            /// Új tekton az ős + A vagy B nevet kapja 
            if (registry.getByName(newNameA) != null) {
                newNameA += "_" + System.currentTimeMillis();
            }
            if (registry.getByName(newNameB) != null) {
                newNameB += "_" + System.currentTimeMillis();
            }

            // Új tektonok regisztrálása és logolása
            registry.register(newNameA, newTectons.get(0));
            registry.register(newNameB, newTectons.get(1));

            logger.logChange("TECTON", original, "BREAK", baseName, newNameA);
            logger.logChange("TECTON", original, "BREAK", baseName, newNameB);

        }
         game.notifyObservers();
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
