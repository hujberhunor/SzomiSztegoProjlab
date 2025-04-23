package com.dino.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dino.core.Fungus;
import com.dino.engine.Game;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.player.Player;
import com.dino.tecton.Tecton;
import com.dino.tecton.TectonFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InitLoader {

    public static void loadFromFile(String filename, Game game) throws Exception {
        JsonObject root = Serializer.loadFromFile(filename);
        loadPlayers(root, game);
        loadBoard(root, game);
    }

    // Játékosok visszatöltése
    private static void loadPlayers(JsonObject root, Game game) {
        JsonArray playerArray = root.getAsJsonArray("players");
        int mycoCount = 0, entoCount = 0;

        for (JsonElement e : playerArray) {
            JsonObject playerObj = e.getAsJsonObject();
            String type = playerObj.get("type").getAsString();

            Player p;
            String name;
            if ("mycologist".equalsIgnoreCase(type)) {
                p = new Mycologist();
                name = "myco_" + mycoCount++;
            } else if ("entomologist".equalsIgnoreCase(type)) {
                p = new Entomologist();
                name = "ento_" + entoCount++;
            } else {
                throw new IllegalArgumentException("Ismeretlen játékos típus: " + type);
            }

            game.getRegistry().register(name, p);
            game.addPlayer(p);
        }
    }

    // Tektonok + belső elemeik visszatöltése
    private static void loadBoard(JsonObject root, Game game) {
        JsonObject boardJson = root.getAsJsonObject("board");
        JsonArray tectonArray = boardJson.getAsJsonArray("tectons");

        for (JsonElement tectonEl : tectonArray) {
            JsonObject tectonJson = tectonEl.getAsJsonObject();

            Tecton t = TectonFactory.fromJson(tectonJson, game);

            game.getBoard().addTecton(t);
            String name = registryNameForTecton(t, game);
            game.getRegistry().register(name, t);

            if (tectonJson.has("spores")) {
                loadSpores(tectonJson, t, game);
            }

            if (tectonJson.has("neighbours")) {
                loadNeighbours(tectonJson, t, game);
            }

            if (tectonJson.has("fungus")) {
                JsonObject fungusJson = tectonJson.getAsJsonObject("fungus");
                // Helyes: Fungus.deserialize(...) a Fungus osztályban legyen!
                t.setFungus(Fungus.deserialize(fungusJson, game.getRegistry()));
            }

            // TODO: később: hyphas, insects
        }
    }

    // Spórák visszatöltése
    private static void loadSpores(JsonObject tectonJson, Tecton t, Game game) {
        JsonObject sporeObj = tectonJson.getAsJsonObject("spores");
        Map<Mycologist, Integer> spores = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : sporeObj.entrySet()) {
            String name = entry.getKey();
            int count = entry.getValue().getAsInt();

            Object obj = game.getRegistry().getByName(name);
            if (obj instanceof Mycologist) {
                spores.put((Mycologist) obj, count);
            } else {
                game.getLogger().logError("Tecton", game.getRegistry().getNameOf(t),
                        "Ismeretlen vagy nem Mycologist típus: " + name);
            }
        }

        t.spores = spores;
    }

    // Szomszédos tektonok visszakötése
    private static void loadNeighbours(JsonObject tectonJson, Tecton t, Game game) {
        if (!tectonJson.has("neighbours"))
            return;

        List<Tecton> neighbourList = new ArrayList<>();
        JsonArray neighbourArray = tectonJson.getAsJsonArray("neighbours");

        for (JsonElement e : neighbourArray) {
            String name = e.getAsString();
            Object obj = game.getRegistry().getByName(name);

            if (obj instanceof Tecton) {
                neighbourList.add((Tecton) obj);
            } else {
                game.getLogger().logError("Tecton", game.getRegistry().getNameOf(t),
                        "Ismeretlen vagy nem Tecton típusú szomszéd: " + name);
            }
        }

        t.setNeighbours(neighbourList);
    }

    // Név generálás, ha még nincs
    private static String registryNameForTecton(Tecton t, Game game) {
        String name = game.getRegistry().getNameOf(t);
        if (name != null)
            return name;
        return "tecton_" + t.hashCode();
    }
}
