package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.InitLoader;
import com.dino.util.Logger;

public class LoadCommand implements Command {
    private final String filename;

    public LoadCommand(String filename) {
        this.filename = filename;
    }

    @Override
    public void execute(Game game, Logger logger) {
        try {
            Game loadedGame = InitLoader.loadFromFile(filename);
            logger.logChange("GAME", loadedGame, "LOAD", "-", "SUCCESS");
        } catch (Exception e) {
            logger.logError("GAME", "LoadCommand", "Failed to load game: " + e.getMessage());
        }
    }

    @Override
    public boolean validate(Game game) {
        return filename != null && !filename.isBlank();
    }

    @Override
    public String toString() {
        return "LOAD " + filename;
    }
}
