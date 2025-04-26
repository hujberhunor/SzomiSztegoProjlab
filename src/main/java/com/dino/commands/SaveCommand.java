package com.dino.commands;

import java.io.IOException;

import com.dino.engine.Game;
import com.dino.util.InitLoader;
import com.dino.util.Logger;
import com.dino.util.Serializer;
import com.google.gson.JsonObject;

public class SaveCommand implements Command {
    private final String filename;

    public SaveCommand(String filename) {
        this.filename = filename;
    }

    @Override
    public void execute(Game game, Logger logger) {
        try {
            JsonObject gameState = InitLoader.serialize(game, game.getNamer());
            Serializer.saveJsonToFile(gameState, filename);
            logger.logChange("GAME", game, "SAVE", "-", "SUCCESS");
        } catch (IOException e) {
            logger.logError("GAME", "SaveCommand", "Failed to save game: " + e.getMessage());
        }
    }

    @Override
    public boolean validate(Game game) {
        return filename != null && !filename.isBlank();
    }

    @Override
    public String toString() {
        return "SAVE " + filename;
    }
}
