package com.dino.util;

import com.dino.engine.Game;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.player.Player;
import com.dino.tecton.Tecton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InitLoader {

    public static void loadFromFile(String filename, Game game) throws Exception {
        JsonObject root = Serializer.loadFromFile(filename);
        int mycoCount = 0;
        int entoCount = 0;

        JsonArray playerArray = root.getAsJsonArray("players");
        for (JsonElement e : playerArray) {
            JsonObject playerObj = e.getAsJsonObject();
            String type = playerObj.get("type").getAsString();

            Player p;
            String generatedName;

            if ("mycologist".equalsIgnoreCase(type)) {
                p = new Mycologist();
                generatedName = "myco_" + mycoCount++;
            } else if ("entomologist".equalsIgnoreCase(type)) {
                p = new Entomologist();
                generatedName = "ento_" + entoCount++;
            } else {
                throw new IllegalArgumentException("Ismeretlen játékos típus: " + type);
            }

            game.getRegistry().register(generatedName, p);
            game.addPlayer(p);

            JsonObject boardJson = root.getAsJsonObject("board");
            JsonArray tectonArray = boardJson.getAsJsonArray("tectons");

            for (JsonElement e : tectonArray) {
                JsonObject tectonObj = e.getAsJsonObject();
                Tecton t = TectonFactory.fromJson(tectonObj, game); // következő lépés: ezt megírni
                game.getBoard().addTecton(t);
                game.getRegistry().register(t.getId(), t); // ha van id-je
            }

        }
    }
}
