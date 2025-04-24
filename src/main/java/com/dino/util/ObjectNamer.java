package com.dino.util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.dino.core.Hexagon;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.player.Player;
import com.dino.tecton.Tecton;

/**
 * Ez az osztály kezeli az entitásokhoz tartozó determinisztikus neveket és
 * regisztrálja is őket
 * az EntityRegistry-n keresztül.
 */
public class ObjectNamer {
    private int insectCounter = 0;
    private int hyphaCounter = 0;
    private int sporeCounter = 0;
    private int fungusCounter = 0;
    private int mycologistCounter = 0;
    private int entomologistCounter = 0;

    private final EntityRegistry registry;

    public ObjectNamer(EntityRegistry registry) {
        this.registry = registry;
    }

    public String tectonName(Tecton t) {
        List<Integer> sortedIds = t.hexagons.stream()
                .map(Hexagon::getId)
                .sorted()
                .collect(Collectors.toList());

        return "tecton_" + sortedIds.stream()
                .map(Object::toString)
                .collect(Collectors.joining("_"));
    }

    public String registerTecton(Tecton t) {
        String name = tectonName(t);
        registry.register(name, t);
        return name;
    }

    public String registerInsect(Entomologist ento, Object insect) {
        String name = "insect_" + ento.hashCode() + "_" + insectCounter++;
        registry.register(name, insect);
        return name;
    }

    public String registerHypha(Object hypha) {
        String name = "hypha_" + hyphaCounter++;
        registry.register(name, hypha);
        return name;
    }

    public String registerSpore(Object spore) {
        String name = "spore_" + sporeCounter++;
        registry.register(name, spore);
        return name;
    }

    public String registerFungus(Object fungus) {
        String name = "fungus_" + fungusCounter++;
        registry.register(name, fungus);
        return name;
    }

    public String registerPlayer(Player p) {
        String name;
        if (p instanceof Mycologist) {
            name = "myco_" + mycologistCounter++;
        } else if (p instanceof Entomologist) {
            name = "ento_" + entomologistCounter++;
        } else {
            name = "player_" + UUID.randomUUID();
        }
        registry.register(name, p);
        return name;
    }

    public String getNameOf(Object obj) {
        return registry.getNameOf(obj);
    }

    public Object getByName(String name) {
        return registry.getByName(name);
    }

    public void reset() {
        insectCounter = hyphaCounter = sporeCounter = fungusCounter = 0;
        mycologistCounter = entomologistCounter = 0;
        // registry-t nem reseteljük automatikusan, mert több komponens használhatja
    }
}
