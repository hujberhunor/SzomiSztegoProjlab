package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.InitLoader;
import com.dino.util.Logger;

public class InitCommand implements Command {
    private final String filename;

    public InitCommand(String filename) {
        this.filename = filename;
    }

    @Override
    public void execute(Game game, Logger logger) {
        try {
            Game newGame = InitLoader.loadFromFile(filename);
            logger.logChange("GAME", newGame, "INIT", "-", "SUCCESS");
        } catch (Exception e) {
            logger.logError("GAME", "InitCommand", "Failed to initialize from file: " + e.getMessage());
        }
    }

    @Override
    public boolean validate(Game game) {
        return filename != null && !filename.isBlank();
    }

    @Override
    public String toString() {
        return "INIT " + filename;
    }
}
