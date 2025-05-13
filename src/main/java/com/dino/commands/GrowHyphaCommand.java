package com.dino.commands;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.engine.Game;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

public class GrowHyphaCommand implements Command {

    private final String fungusName;
    private final String nextTectonName;

    public GrowHyphaCommand(String fungusName, String nextTectonString) {
        this.fungusName = fungusName;
        this.nextTectonName = nextTectonString;
    }

    @Override
    public void execute(Game game, Logger logger) {
        EntityRegistry registry = game.getRegistry();

        Fungus fungus = (Fungus) registry.getByName(fungusName);
        String fungusId = registry.getNameOf(fungus);
        Mycologist mycologist = fungus.getSpecies();
        Tecton startTecton = fungus.getTecton();
        Tecton nextTecton = (Tecton) registry.getByName(nextTectonName);

        Hypha hypha = new Hypha(mycologist, fungus);
        boolean success = hypha.continueHypha(nextTecton);

        if(success) {
            String baseName = "hypha_" + fungusId;
            String name = baseName;
            int i = 1;
            while (registry.getByName(name) != null) {
                name = baseName + "_" + i++;
            }
            registry.register(name, hypha);

            logger.logChange("HYPHA", hypha, "CREATE", fungusId, startTecton.toString());

            fungus.getSpecies().decreaseActions();
        }
    }

    @Override
    public boolean validate(Game game) {
        // return game.getRegistry().getByName(fungusName) instanceof Fungus;
        return true;
    }

    @Override
    public String toString() {
        return "GROW_HYPHA " + fungusName;
    }
}

