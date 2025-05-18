package com.dino.commands;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.engine.Game;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.dino.util.EntityRegistry;
import com.dino.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        Tecton startTecton = fungus.getTecton();
        Tecton nextTecton = (Tecton) registry.getByName(nextTectonName);
        List<Tecton> tectonsAsList = Arrays.asList(startTecton, nextTecton);

        boolean success = fungus.growHypha(tectonsAsList);

        if(success) {
            Hypha hypha = fungus.getHyphas().get(0);

            logger.logChange("HYPHA", hypha, "CREATE", fungusName, startTecton.toString());

            fungus.getSpecies().decreaseActions();
        }
         game.notifyObservers();
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

