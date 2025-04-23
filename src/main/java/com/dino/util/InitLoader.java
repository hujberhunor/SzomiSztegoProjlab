package com.dino.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.core.Insect;
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
        Logger logger = game.getLogger();
        JsonObject root = Serializer.loadFromFile(filename);

        loadPlayers(root, game, logger);
        loadBoard(root, game, logger);
        loadHyphas(root, game, logger);
        loadInsects(root, game, logger); 
    }

    // Játékosok visszatöltése
    public static void loadPlayers(JsonObject root, Game game, Logger logger) {
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
                logger.logError("Init", "player", "Ismeretlen játékos típus: " + type);
                continue;
            }

            game.getRegistry().register(name, p);
            game.addPlayer(p);
        }
    }

    // Tektonok és beágyazott komponensek visszatöltése
    public static void loadBoard(JsonObject root, Game game, Logger logger) {
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
                t.setFungus(Fungus.deserialize(fungusJson, game.getRegistry(), logger));
            }
        }
    }

    // Spórák visszatöltése
    public static void loadSpores(JsonObject tectonJson, Tecton t, Game game) {
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
    public static void loadNeighbours(JsonObject tectonJson, Tecton t, Game game) {
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

    // Hyphák visszatöltése
    public static void loadHyphas(JsonObject root, Game game, Logger logger) {
        if (!root.has("hyphas"))
            return;

        JsonArray hyphaArray = root.getAsJsonArray("hyphas");
        int hyphaCount = 0;

        for (JsonElement e : hyphaArray) {
            JsonObject hyphaObj = e.getAsJsonObject();
            Hypha h = Hypha.deserialize(hyphaObj, game.getRegistry(), logger);
            String name = "hypha_" + hyphaCount++;
            game.getRegistry().register(name, h);
        }
    }

    // Név generálása, ha még nincs
    private static String registryNameForTecton(Tecton t, Game game) {
        String name = game.getRegistry().getNameOf(t);
        return name != null ? name : "tecton_" + t.hashCode();
    }

    public static void loadInsects(JsonObject root, Game game, Logger logger) {
        if (!root.has("insects"))
            return;

        JsonArray insectArray = root.getAsJsonArray("insects");
        int insectCount = 0;

        for (JsonElement e : insectArray) {
            JsonObject insectObj = e.getAsJsonObject();
            Insect insect = Insect.deserialize(insectObj, game.getRegistry(), logger);

            if (insect != null) {
                String name = "insect_" + insectCount++;
                game.getRegistry().register(name, insect);
            } else {
                logger.logError("Init", "insects", "Insect példányosítás sikertelen volt (null).");
            }
        }
    }
}
