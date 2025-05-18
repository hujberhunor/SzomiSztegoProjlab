package com.dino.commands;

import java.util.List;

import com.dino.core.Hexagon;
import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;
import com.dino.view.GuiBoard;
import com.dino.view.ModelObserver;

import javafx.application.Platform;

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
            
            if (original == null) {
                logger.logError("TECTON", tectonName, "Tecton Not Found");
                return;
            }
            
            if (original.hexagons.size() > 0) {
                StringBuilder hexIds = new StringBuilder();
                for (Hexagon h : original.hexagons) {
                    hexIds.append(h.getId()).append(", ");
                }
            }
            
            List<Tecton> newTectons = original.split(-1);
            
            
            if (newTectons.isEmpty()) {
                StringBuilder reason = new StringBuilder("Tecton cannot break: ");
                
                if (original.hexagons.size() <= 1) {
                    reason.append("not enough hexagon (" + original.hexagons.size() + ")");
                } else if (original.breakCount >= 2) {
                    reason.append("cannot break anymore (" + original.breakCount + ")");
                } else if (!original.insects.isEmpty()) {
                    reason.append("there is an insect on it (" + original.insects.size());
                } else {
                    reason.append("Unknown reason");
                }
                
                logger.logError("TECTON", tectonName, reason.toString());
                return;
            }
            
            // STRUKTÚRA MÓDOSÍTÁS
            try {
                boolean removed = game.getBoard().getAllTectons().remove(original);
                
                // ÚJ TECTONOK ELNEVEZÉSE
                String newNameA = tectonName + "_A";
                String newNameB = tectonName + "_B";
                
                if (registry.isNameRegistered(newNameA)) {
                    newNameA = tectonName + "_A_" + System.currentTimeMillis();
                }
                if (registry.isNameRegistered(newNameB)) {
                    newNameB = tectonName + "_B_" + System.currentTimeMillis();
                }
                
                // REGISZTRÁCIÓ
                registry.register(newNameA, newTectons.get(0));
                registry.register(newNameB, newTectons.get(1));
                
                // GAMEBOARDHOZ ADDOLÁS
                game.getBoard().getAllTectons().add(newTectons.get(0));
                game.getBoard().getAllTectons().add(newTectons.get(1));
                
                logger.logChange("GAMEBOARD", original, "BREAK", tectonName, 
                        newNameA + ", " + newNameB);
            } catch (Exception e) {
                System.err.println("Error with the structure: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            // GUI FRISSÍTÉS - Platform.runLater-rel biztosítjuk a megfelelő szálon való futást
            Platform.runLater(() -> {
                try {
                    // GUI BOARD KERESÉSE
                    GuiBoard guiBoard = null;
                    for (ModelObserver observer : game.getObservers()) {
                        if (observer instanceof GuiBoard) {
                            guiBoard = (GuiBoard) observer;
                            break;
                        }
                    }
                    
                    if (guiBoard != null) {
                        final Tecton t1 = newTectons.get(0);
                        final Tecton t2 = newTectons.get(1);
                        guiBoard.recolorTecton(t1, t2);
                    } else {
                        System.out.println("Cannot find GUIBOARD");
                    }
                    
                    game.notifyObservers();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Critical Error: " + e.getMessage());
            e.printStackTrace();
            logger.logError("BREAK_TECTON", tectonName, "Kritikus hiba: " + e.getMessage());
        }
    }

    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        Object obj = reg.getByName(tectonName);
        boolean valid = obj instanceof Tecton;
        System.out.println("Validáció: " + tectonName + " -> " + 
                          (valid ? "Érvényes Tecton" : "Érvénytelen vagy nem található"));
        return valid;
    }

    @Override
    public String toString() {
        return "BREAK_TECTON " + tectonName;
    }
}