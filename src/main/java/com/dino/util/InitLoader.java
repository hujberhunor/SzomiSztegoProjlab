package com.dino.util;

import com.dino.core.Fungus;
import com.dino.core.Hexagon;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.core.Spore;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InitLoader {

    public static JsonObject serialize(Game game, ObjectNamer namer) {
        JsonObject root = new JsonObject();

        // --- Mycologists ---
        List<Mycologist> mycologists = game.getAllMycologists();
        root.add(
            "mycologists",
            SerializerUtil.toJsonArray(mycologists, m -> {
                namer.register(m);
                return m.serialize(namer);
            })
        );

        // --- Entomologists ---
        List<Entomologist> entomologists = game.getAllEntomologists();
        root.add(
            "entomologists",
            SerializerUtil.toJsonArray(entomologists, e -> {
                namer.register(e);
                return e.serialize(namer);
            })
        );

        // --- Tectons ---
        List<Tecton> tectons = game.getBoard().getAllTectons();
        root.add(
            "tectons",
            SerializerUtil.toJsonArray(tectons, t -> {
                namer.register(t);
                return t.serialize(namer);
            })
        );

        // --- Hyphas, Fungi, Insects (Tecton-okon keresztül) ---
        Set<Hypha> allHyphas = new HashSet<>();
        Set<Fungus> allFungi = new HashSet<>();
        Set<Insect> allInsects = new HashSet<>();

        for (Tecton t : tectons) {
            // Add hyphas
            if (t.getHyphas() != null) {
                for (Hypha h : t.getHyphas()) {
                    if (h != null) {
                        namer.register(h);
                        allHyphas.add(h);
                    }
                }
            }

            // Add fungus
            Fungus f = t.getFungus();
            if (f != null) {
                namer.register(f);
                allFungi.add(f);
            }

            // Add insects
            if (t.getInsects() != null) {
                for (Insect i : t.getInsects()) {
                    if (i != null) {
                        namer.register(i);
                        allInsects.add(i);
                    }
                }
            }
        }

        root.add(
            "hyphas",
            SerializerUtil.toJsonArray(new ArrayList<>(allHyphas), h ->
                h.serialize(namer)
            )
        );
        root.add(
            "fungi",
            SerializerUtil.toJsonArray(new ArrayList<>(allFungi), f ->
                f.serialize(namer)
            )
        );
        root.add(
            "insects",
            SerializerUtil.toJsonArray(new ArrayList<>(allInsects), i ->
                i.serialize(namer)
            )
        );

        // --- Game State ---
        JsonObject gameState = new JsonObject();
        gameState.addProperty("round", game.getCurrentRound());
        gameState.addProperty("totalRounds", game.getTotalRounds());

        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer != null) {
            gameState.addProperty(
                "currentPlayer",
                namer.getName(currentPlayer)
            );
        } else {
            gameState.add("currentPlayer", null);
        }

        root.add("gameState", gameState);

        return root;
    }

    public static Game loadFromFile(String filename) throws IOException {
        JsonObject json = Serializer.loadFromFile(filename);

        // 1. Game példány létrehozása
        int totalRounds = json
            .getAsJsonObject("gameState")
            .get("totalRounds")
            .getAsInt();
        Game game = new Game(totalRounds);
        EntityRegistry registry = game.getRegistry();
        ObjectNamer namer = ObjectNamer.getInstance();

        // 2. Mycologist-ek visszatöltése
        JsonArray mycologists = json.getAsJsonArray("mycologists");
        for (JsonElement elem : mycologists) {
            JsonObject obj = elem.getAsJsonObject();
            Mycologist m = new Mycologist();
            m.score = obj.get("score").getAsInt();
            m.remainingActions = obj.get("remainingActions").getAsInt();
            if (obj.has("name") && !obj.get("name").isJsonNull()) {
                String playerName = obj.get("name").getAsString();
                m.setName(playerName);
                namer.register(playerName, m);
            } else {
                namer.register(m);
            }
            game.addPlayer(m);
        }

        // 3. Entomologist-ek visszatöltése
        JsonArray entomologists = json.getAsJsonArray("entomologists");
        for (JsonElement elem : entomologists) {
            JsonObject obj = elem.getAsJsonObject();
            Entomologist e = new Entomologist();
            e.score = obj.get("score").getAsInt();
            e.remainingActions = obj.get("remainingActions").getAsInt();
            if (obj.has("name") && !obj.get("name").isJsonNull()) {
                String playerName = obj.get("name").getAsString();
                e.setName(playerName);
                namer.register(playerName, e);
            } else {
                namer.register(e);
            }
            game.addPlayer(e);
        }

        // 4. Tecton-ok visszatöltése (neighbours-t később!)
        Map<String, Tecton> tectonMap = new HashMap<>();
        JsonArray tectons = json.getAsJsonArray("tectons");
        for (JsonElement elem : tectons) {
            JsonObject obj = elem.getAsJsonObject();
            Tecton t = createTectonFromJson(obj);
            String tectonName = obj.get("name").getAsString();
            namer.register(tectonName, t);
            tectonMap.put(tectonName, t);
            game.getBoard().getAllTectons().add(t);
        }

        // 5. Neighbours linking (második kör)
        for (JsonElement elem : tectons) {
            JsonObject obj = elem.getAsJsonObject();
            String tectonName = obj.get("name").getAsString();
            Tecton t = (Tecton) registry.getByName(tectonName);

            if (
                t != null &&
                obj.has("neighbours") &&
                !obj.get("neighbours").isJsonNull()
            ) {
                JsonArray neighbours = obj.getAsJsonArray("neighbours");
                for (JsonElement neighbourElem : neighbours) {
                    String neighbourName = neighbourElem.getAsString();
                    Tecton neighbour = (Tecton) registry.getByName(
                        neighbourName
                    );
                    if (
                        neighbour != null &&
                        !t.getNeighbours().contains(neighbour)
                    ) {
                        t.getNeighbours().add(neighbour);
                        if (!neighbour.getNeighbours().contains(t)) {
                            neighbour.getNeighbours().add(t);
                        }
                    }
                }
            }
        }

        // 6. Fungi visszatöltése
        Map<String, Fungus> fungiMap = new HashMap<>();
        if (json.has("fungi") && !json.get("fungi").isJsonNull()) {
            JsonArray fungi = json.getAsJsonArray("fungi");
            for (JsonElement elem : fungi) {
                JsonObject obj = elem.getAsJsonObject();
                String fungusName = obj.get("name").getAsString();

                Fungus f = new Fungus();

                // Set Mycologist
                if (obj.has("species") && !obj.get("species").isJsonNull()) {
                    String speciesName = obj.get("species").getAsString();
                    Mycologist mycologist = (Mycologist) registry.getByName(
                        speciesName
                    );
                    if (mycologist != null) {
                        f.setSpecies(mycologist);
                        mycologist.getMushrooms().add(f);
                    }
                }

                // Set charge and lifespan
                if (obj.has("charge")) f.setCharge(
                    obj.get("charge").getAsInt()
                );
                if (obj.has("lifespan")) f.setLifespan(
                    obj.get("lifespan").getAsInt()
                );

                // Set tecton reference
                if (obj.has("tecton") && !obj.get("tecton").isJsonNull()) {
                    String tectonName = obj.get("tecton").getAsString();
                    Tecton tecton = (Tecton) registry.getByName(tectonName);
                    if (tecton != null) {
                        f.setTecton(tecton);
                        tecton.setFungus(f);
                    }
                }

                namer.register(fungusName, f);
                fungiMap.put(fungusName, f);
            }
        }

        // 7. Hyphas visszatöltése
        Map<String, Hypha> hyphasMap = new HashMap<>();
        if (json.has("hyphas") && !json.get("hyphas").isJsonNull()) {
            JsonArray hyphas = json.getAsJsonArray("hyphas");
            for (JsonElement elem : hyphas) {
                JsonObject obj = elem.getAsJsonObject();
                String hyphaName = obj.get("name").getAsString();

                Hypha h = new Hypha();

                // Set Mycologist
                if (
                    obj.has("mycologist") && !obj.get("mycologist").isJsonNull()
                ) {
                    String mycologistName = obj.get("mycologist").getAsString();
                    Mycologist mycologist = (Mycologist) registry.getByName(
                        mycologistName
                    );
                    if (mycologist != null) {
                        h.setMychologist(mycologist);
                    }
                }

                // Set Fungus
                if (obj.has("fungus") && !obj.get("fungus").isJsonNull()) {
                    String fungusName = obj.get("fungus").getAsString();
                    Fungus fungus = (Fungus) registry.getByName(fungusName);
                    if (fungus != null) {
                        h.setFungus(fungus);
                        fungus.getHyphas().add(h);
                    }
                }

                // Set lifespan
                if (obj.has("lifespan")) h.setLifespan(
                    obj.get("lifespan").getAsInt()
                );

                // Add to tectons
                if (obj.has("tectons") && !obj.get("tectons").isJsonNull()) {
                    JsonArray tectonList = obj.getAsJsonArray("tectons");
                    for (JsonElement tectonElem : tectonList) {
                        String tectonName = tectonElem.getAsString();
                        Tecton tecton = (Tecton) registry.getByName(tectonName);
                        if (tecton != null) {
                            h.getTectons().add(tecton);
                            tecton.getHyphas().add(h);
                        }
                    }
                }

                namer.register(hyphaName, h);
                hyphasMap.put(hyphaName, h);
            }
        }

        // 8. Insects visszatöltése
        if (json.has("insects") && !json.get("insects").isJsonNull()) {
            JsonArray insects = json.getAsJsonArray("insects");
            for (JsonElement elem : insects) {
                JsonObject obj = elem.getAsJsonObject();
                String insectName = obj.get("name").getAsString();

                // Get owner and current tecton
                String ownerName = obj.has("owner")
                    ? obj.get("owner").getAsString()
                    : null;
                Entomologist owner = (Entomologist) registry.getByName(
                    ownerName
                );

                String tectonName = obj.has("currentTecton")
                    ? obj.get("currentTecton").getAsString()
                    : null;
                Tecton currentTecton = (Tecton) registry.getByName(tectonName);

                if (owner != null && currentTecton != null) {
                    Insect insect = new Insect(owner, currentTecton);

                    // Add to lists
                    owner.getInsects().add(insect);
                    currentTecton.getInsects().add(insect);

                    // Add effects if present
                    if (
                        obj.has("effects") && !obj.get("effects").isJsonNull()
                    ) {
                        JsonArray effectsArray = obj.getAsJsonArray("effects");
                        for (JsonElement effectElem : effectsArray) {
                            try {
                                if (effectElem.isJsonObject()) {
                                    JsonObject effectObj =
                                        effectElem.getAsJsonObject();
                                    Spore spore = SporeDeserializer.deserialize(
                                        effectObj,
                                        registry
                                    );
                                    if (spore != null) {
                                        insect.getEffects().add(spore);
                                    }
                                } else if (effectElem.isJsonPrimitive()) {
                                    String effectName =
                                        effectElem.getAsString();
                                    Spore spore = (Spore) registry.getByName(
                                        effectName
                                    );
                                    if (spore != null) {
                                        insect.getEffects().add(spore);
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println(
                                    "Error loading effect: " + e.getMessage()
                                );
                            }
                        }
                    }

                    namer.register(insectName, insect);
                }
            }
        }

        // 9. GameState beállítása
        if (json.has("gameState") && !json.get("gameState").isJsonNull()) {
            JsonObject gameState = json.getAsJsonObject("gameState");

            if (gameState.has("round")) {
                game.setCurrentTurn(gameState.get("round").getAsInt());
            }

            if (gameState.has("totalRounds")) {
                game.settotalRounds(gameState.get("totalRounds").getAsInt());
            }

            if (
                gameState.has("currentPlayer") &&
                !gameState.get("currentPlayer").isJsonNull()
            ) {
                String currentPlayerName = gameState
                    .get("currentPlayer")
                    .getAsString();
                Player currentPlayer = (Player) registry.getByName(
                    currentPlayerName
                );
                if (currentPlayer != null) {
                    game.setCurrentPlayer(currentPlayer);
                }
            }
        }

        return game;
    }

    /**
     * Creates a Tecton instance from a JSON object.
     */
    private static Tecton createTectonFromJson(JsonObject obj) {
        String type = obj.has("type")
            ? obj.get("type").getAsString()
            : "SingleHyphaTecton";
        Tecton t;

        // Create the appropriate tecton type
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
                t = new SingleHyphaTecton(); // Default fallback
                break;
        }

        // Set common properties
        if (obj.has("breakChance")) {
            t.breakChance = obj.get("breakChance").getAsDouble();
        }

        // Add hexagons
        if (obj.has("hexagons") && !obj.get("hexagons").isJsonNull()) {
            List<Hexagon> hexagons = new ArrayList<>();
            JsonArray hexArray = obj.getAsJsonArray("hexagons");
            for (JsonElement hexElem : hexArray) {
                try {
                    int id = Integer.parseInt(hexElem.getAsString());
                    hexagons.add(new Hexagon(id));
                } catch (NumberFormatException e) {
                    // Skip invalid hexagons
                }
            }
            t.hexagons = hexagons;
        }

        // Set type-specific properties
        if (t instanceof ShortHyphaTecton) {
            ShortHyphaTecton sht = (ShortHyphaTecton) t;
            if (obj.has("hyphaLifespan")) {
                sht.setHyphaLifespan(obj.get("hyphaLifespan").getAsInt());
            }
            if (obj.has("hyphaLimit")) {
                sht.setHyphaLimit(obj.get("hyphaLimit").getAsInt());
            }
        } else if (t instanceof SingleHyphaTecton) {
            SingleHyphaTecton sit = (SingleHyphaTecton) t;
            if (obj.has("hyphaLimit")) {
                sit.setHyphaLimit(obj.get("hyphaLimit").getAsInt());
            }
        } else if (t instanceof KeepHyphaTecton) {
            KeepHyphaTecton kht = (KeepHyphaTecton) t;
            if (obj.has("hyphaLifespan")) {
                kht.setHyphaLifespan(obj.get("hyphaLifespan").getAsInt());
            }
            if (obj.has("hyphaLimit")) {
                kht.setHyphaLimit(obj.get("hyphaLimit").getAsInt());
            }
        }

        return t;
    }
}
