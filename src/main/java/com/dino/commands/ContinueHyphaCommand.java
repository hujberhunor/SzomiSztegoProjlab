package com.dino.commands;

import com.dino.core.Hypha;
import com.dino.engine.Game;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class ContinueHyphaCommand implements Command {

    private final String hyphaName;
    private final String targetTectonName;

    public ContinueHyphaCommand(String hyphaName, String targetTectonName) {
        this.hyphaName = hyphaName;
        this.targetTectonName = targetTectonName;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();
        Hypha hypha = (Hypha) registry.getByName(hyphaName);
        Tecton target = (Tecton) registry.getByName(targetTectonName);
        String hyphaId = registry.getNameOf(hypha);

        boolean success = hypha.continueHypha(target);
        if (success) {
            logger.logChange("HYPHA", hypha, "CONTINUE", "-", targetTectonName);
        } else {
            logger.logError("HYPHA", hyphaId, "Cannot continue hypha: target is not neighbor.");
        }
    }

    @Override
    public boolean validate(Game game) {
        EntityRegistry reg = game.getRegistry();
        return reg.getByName(hyphaName) instanceof Hypha
            && reg.getByName(targetTectonName) instanceof Tecton;
    }

    @Override
    public String toString() {
        return "CONTINUE_HYPHA " + hyphaName + " " + targetTectonName;
    }
}
