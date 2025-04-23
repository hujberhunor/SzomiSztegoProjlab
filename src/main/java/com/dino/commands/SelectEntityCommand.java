package com.dino.commands;

import com.dino.engine.Game;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class SelectEntityCommand implements Command {

    private final String entityName;

    public SelectEntityCommand(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        Object entity = registry.getByName(entityName);

        if (entity != null) {
            logger.logChange("GAME", game, "SELECTED_ENTITY", "-", entityName);
            game.setSelectedEntity(entity);
        } else {
            logger.logError("GAME", "Game", "Entity not found: " + entityName);
        }
    }

    /**
     * Validálja, hogy az entitás létezik-e a registry-ben
     */
    @Override
    public boolean validate(Game game) {
        return game.getRegistry().getByName(entityName) != null;
    }

    @Override
    public String toString() {
        return "SELECT_ENTITY " + entityName;
    }
}
