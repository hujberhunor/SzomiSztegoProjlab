package com.dino.commands;

import java.util.List;

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
        
        if (original == null) {
            logger.logError("TECTON", tectonName, "Tecton nem található");
            return;
        }
        
        // 1. TÖRÉS VÉGREHAJTÁSA - garantált törés a negatív értékkel
        List<Tecton> newTectons = original.split(-1);
        
        System.out.println("Törés után létrejött tectonok száma: " + newTectons.size());
        
        // 2. ELLENŐRZÉS: Ha a split nem hozott létre új tektonokat
        if (newTectons.isEmpty()) {
            logger.logError("TECTON", tectonName, "Tecton nem törhet el: " +
                    (original.insects.isEmpty() ? "nincs megadva ok" : 
                    "rovar van rajta (" + original.insects.size() + " db)"));
            return;
        }
        
        game.getBoard().getAllTectons().remove(original);
        
        // ÚJ TECTONOK ELNEVEZÉSE ÉS REGISZTRÁLÁSA
        String newNameA = tectonName + "_A";
        String newNameB = tectonName + "_B";
        
        if (registry.isNameRegistered(newNameA)) {
            newNameA = tectonName + "_A_" + System.currentTimeMillis();
        }
        if (registry.isNameRegistered(newNameB)) {
            newNameB = tectonName + "_B_" + System.currentTimeMillis();
        }
        
        registry.register(newNameA, newTectons.get(0));
        registry.register(newNameB, newTectons.get(1));
        
        System.out.println("Regisztrált nevek: " + newNameA + ", " + newNameB);
        
        // 5. ÚJ TECTONOK ADDOLVA A GAMEBOARDHOZ
        game.getBoard().getAllTectons().add(newTectons.get(0));
        game.getBoard().getAllTectons().add(newTectons.get(1));
        
        System.out.println("Játéktábla tectonok száma: " + game.getBoard().getAllTectons().size());
        
        logger.logChange("GAMEBOARD", original, "BREAK", tectonName, 
                newNameA + ", " + newNameB);
        
        //KERESSÜK A GUI BOARD PÉLDÁNYÁT
        GuiBoard guiBoard = null;
        for (ModelObserver observer : game.getObservers()) {
            if (observer instanceof GuiBoard) {
                guiBoard = (GuiBoard) observer;
                System.out.println("GuiBoard megtalálva!");
                break;
            }
        }
        
        //ÚJRASZÍNEZÉS A GUIN
        if (guiBoard != null) {
            final Tecton t1 = newTectons.get(0);
            final Tecton t2 = newTectons.get(1);
            System.out.println("Újraszínezés meghívása...");
            guiBoard.recolorTecton(t1, t2);
        } else {
            System.out.println("FIGYELEM: GuiBoard nem található a megfigyelők között!");
        }
        
        //ÖSSZES OBSERVER ÉRTESÍTÉSE
        System.out.println("Játék megfigyelők értesítése...");
        game.notifyObservers();
        
        System.out.println("=== TÖRÉSI PARANCS BEFEJEZVE ===");
    }

    /**
     * Validálja, hogy a megadott Tecton objektum létezik
     */
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