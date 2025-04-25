package com.dino.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.engine.Game;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;
import com.google.gson.JsonObject;

public class InitLoader {

    public static JsonObject serialize(Game game, ObjectNamer namer) {
        JsonObject root = new JsonObject();

        // --- Mycologists ---
        List<Mycologist> mycologists = game.getAllMycologists();
        root.add("mycologists", SerializerUtil.toJsonArray(mycologists, m -> {
            namer.register(m);
            return m.serialize(namer);
        }));

        // --- Entomologists ---
        List<Entomologist> entomologists = game.getAllEntomologists();
        root.add("entomologists", SerializerUtil.toJsonArray(entomologists, e -> {
            namer.register(e);
            return e.serialize(namer);
        }));

        // --- Tectons ---
        List<Tecton> tectons = game.getBoard().getAllTectons();
        root.add("tectons", SerializerUtil.toJsonArray(tectons, t -> {
            namer.register(t);
            return t.serialize(namer);
        }));

        // --- Hyphas, Fungi, Insects (Tecton-okon keresztül) ---
        Set<Hypha> allHyphas = new HashSet<>();
        Set<Fungus> allFungi = new HashSet<>();
        Set<Insect> allInsects = new HashSet<>();

        for (Tecton t : tectons) {
            allHyphas.addAll(t.getHyphas());

            Fungus f = t.getFungus();
            if (f != null) allFungi.add(f);

            allInsects.addAll(t.getInsects());
        }

        root.add("hyphas", SerializerUtil.toJsonArray(new ArrayList<>(allHyphas), h -> {
            namer.register(h);
            return h.serialize(namer);
        }));

        root.add("fungi", SerializerUtil.toJsonArray(new ArrayList<>(allFungi), f -> {
            namer.register(f);
            return f.serialize(namer);
        }));

        root.add("insects", SerializerUtil.toJsonArray(new ArrayList<>(allInsects), i -> {
            namer.register(i);
            return i.serialize(namer);
        }));

        // --- Game State ---
        JsonObject gameState = new JsonObject();
        gameState.addProperty("round", game.getCurrentTurn());
        gameState.addProperty("totalRounds", game.gettotalRounds());
        gameState.addProperty("currentPlayer", namer.getName(game.getCurrentPlayer()));
        root.add("gameState", gameState);

        return root;
    }

    public static void loadFromFile(String filename, Game game) throws Exception {
        // TODO: deszerializáció implementálása
    }
}
