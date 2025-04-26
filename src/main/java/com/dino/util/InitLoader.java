package com.dino.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dino.core.Hexagon;
import com.dino.engine.Game;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.player.Player;
import com.dino.tecton.InfiniteHyphaTecton;
import com.dino.tecton.KeepHyphaTecton;
import com.dino.tecton.NoFungiTecton;
import com.dino.tecton.ShortHyphaTecton;
import com.dino.tecton.SingleHyphaTecton;
import com.dino.tecton.Tecton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InitLoader {

    public static Game loadFromFile(String filename) throws IOException {
        JsonObject json = Serializer.loadFromFile(filename);

        // 1. Game példány létrehozása
        int totalRounds = json.getAsJsonObject("gameState").get("totalRounds").getAsInt();
        Game game = new Game(totalRounds);
        EntityRegistry registry = game.getRegistry();
        ObjectNamer namer = ObjectNamer.getInstance(registry);

        // 2. Mycologist-ek visszatöltése
        JsonArray mycologists = json.getAsJsonArray("mycologists");
        for (JsonElement elem : mycologists) {
            JsonObject obj = elem.getAsJsonObject();
            Mycologist m = new Mycologist();
            m.score = obj.get("score").getAsInt();
            m.remainingActions = obj.get("remainingActions").getAsInt();
            namer.register(obj.get("name").getAsString(), m);
            game.getPlayers().add(m);
        }

        // 3. Entomologist-ek visszatöltése
        JsonArray entomologists = json.getAsJsonArray("entomologists");
        for (JsonElement elem : entomologists) {
            JsonObject obj = elem.getAsJsonObject();
            Entomologist e = new Entomologist();
            e.score = obj.get("score").getAsInt();
            e.remainingActions = obj.get("remainingActions").getAsInt();
            namer.register(obj.get("name").getAsString(), e);
            game.getPlayers().add(e);
        }

        // 4. Tecton-ok visszatöltése (neighbours-t később!)
        Map<String, Tecton> tectonMap = new HashMap<>();
        JsonArray tectons = json.getAsJsonArray("tectons");
        for (JsonElement elem : tectons) {
            JsonObject obj = elem.getAsJsonObject();
            Tecton t = createTectonFromJson(obj);
            namer.register(obj.get("name").getAsString(), t);
            tectonMap.put(obj.get("name").getAsString(), t);
            game.getBoard().getAllTectons().add(t);
        }

        // 5. Neighbours linking (második kör)
        int i = 0;
        for (JsonElement elem : tectons) {
            JsonObject obj = elem.getAsJsonObject();
            Tecton t = game.getBoard().getAllTectons().get(i++);
            JsonArray neighbours = obj.getAsJsonArray("neighbours");
            for (JsonElement neighbourElem : neighbours) {
                String neighbourName = neighbourElem.getAsString();
                Tecton neighbour = (Tecton) registry.getByName(neighbourName);
                if (neighbour != null && !t.getNeighbours().contains(neighbour)) {
                    Tecton.connectTectons(t, neighbour);
                }
            }
        }

        // 6. Hyphas, Fungi, Insects visszatöltés (később kibővíthető!)
        // TODO: ha szükséges, majd most helyezzük vissza a hypha / fungi / insect objektumokat is

        // 7. GameState visszatöltés
        JsonObject gameState = json.getAsJsonObject("gameState");
        game.settotalRounds(gameState.get("totalRounds").getAsInt());
        game.setCurrentTurn(gameState.get("round").getAsInt());
        String currentPlayerName = gameState.get("currentPlayer").getAsString();
        game.setCurrentPlayer((Player) registry.getByName(currentPlayerName));

        return game;
    }

    private static Tecton createTectonFromJson(JsonObject obj) {
    String type = obj.get("type").getAsString();
    Tecton t;
    switch (type) {
        case "ShortHyphaTecton":
            t = new ShortHyphaTecton();
            break;
        case "SingleHyphaTecton":
            t = new SingleHyphaTecton();
            break;
        case "InfiniteHyphaTecton":
            t = new InfiniteHyphaTecton();
            break;
        case "KeepHyphaTecton":
            t = new KeepHyphaTecton();
            break;
        case "NoFungiTecton":
            t = new NoFungiTecton();
            break;
        default:
            throw new IllegalArgumentException("Ismeretlen tecton típus: " + type);
    }

    t.breakChance = obj.get("breakChance").getAsDouble();

    // Speciális attribútumok CSAK bizonyos leszármazottaknál
    if (t instanceof ShortHyphaTecton || t instanceof SingleHyphaTecton || t instanceof KeepHyphaTecton) {
        if (obj.has("hyphaLimit")) {
            int limit = obj.get("hyphaLimit").getAsInt();
            if (t instanceof ShortHyphaTecton) ((ShortHyphaTecton) t).setHyphaLimit(limit);
            if (t instanceof SingleHyphaTecton) ((SingleHyphaTecton) t).setHyphaLimit(limit);
            if (t instanceof KeepHyphaTecton) ((KeepHyphaTecton) t).setHyphaLimit(limit);
        }
    }
    if (t instanceof ShortHyphaTecton || t instanceof KeepHyphaTecton) {
        if (obj.has("hyphaLifespan")) {
            int lifespan = obj.get("hyphaLifespan").getAsInt();
            if (t instanceof ShortHyphaTecton) ((ShortHyphaTecton) t).setHyphaLifespan(lifespan);
            if (t instanceof KeepHyphaTecton) ((KeepHyphaTecton) t).setHyphaLifespan(lifespan);
        }
    }

    List<Hexagon> hexagons = new ArrayList<>();
    for (JsonElement hexElem : obj.getAsJsonArray("hexagons")) {
        int id = hexElem.getAsInt();
        hexagons.add(new Hexagon(id));
    }
    t.hexagons = hexagons;

    return t;
}

}
