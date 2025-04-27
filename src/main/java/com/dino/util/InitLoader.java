package com.dino.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            if (f != null)
                allFungi.add(f);

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
        gameState.addProperty("round", game.getCurrentRound());
        gameState.addProperty("totalRounds", game.getTotalRounds());
        gameState.addProperty("currentPlayer", namer.getName(game.getCurrentPlayer()));
        root.add("gameState", gameState);

        return root;
    }

    public static Game loadFromFile(String filename) throws IOException {
        JsonObject json = Serializer.loadFromFile(filename);

        // 1. Game példány létrehozása
        int totalRounds = json.getAsJsonObject("gameState").get("totalRounds").getAsInt();
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
        // 6.1 Hyphas visszatöltése
        if (json.has("hyphas")) {
            JsonArray hyphas = json.getAsJsonArray("hyphas");
            for (JsonElement elem : hyphas) {
                JsonObject obj = elem.getAsJsonObject();
                Hypha h = new Hypha();

                // Hypha tectonjainak összekötése
                JsonArray connectedTectons = obj.getAsJsonArray("tectons");
                for (JsonElement tectonElem : connectedTectons) {
                    String tectonName = tectonElem.getAsString();
                    Tecton tecton = (Tecton) registry.getByName(tectonName);
                    if (tecton != null) {
                        h.getTectons().add(tecton);
                        tecton.addHypha(h);
                    }
                }

                // Mycologist hozzárendelése, ha van
                if (obj.has("mycologist")) {
                    String mycologistName = obj.get("mycologist").getAsString();
                    Mycologist mycologist = (Mycologist) registry.getByName(mycologistName);
                    if (mycologist != null) {
                        h.setMychologist(mycologist);
                    }
                }

                // Lifespan, ha van
                if (obj.has("lifespan")) {
                    h.setLifespan(obj.get("lifespan").getAsInt());
                }
            }
        }

        // 6.2 Fungi visszatöltése
        if (json.has("fungi")) {
            JsonArray fungi = json.getAsJsonArray("fungi");
            for (JsonElement elem : fungi) {
                JsonObject obj = elem.getAsJsonObject();
                Fungus f = new Fungus();

                // Mycologist
                String speciesName = obj.get("species").getAsString();
                Mycologist species = (Mycologist) registry.getByName(speciesName);
                f.setSpecies(species);

                // Charge és Lifespan
                f.setCharge(obj.get("charge").getAsInt());
                f.setLifespan(obj.get("lifespan").getAsInt());

                // Hyphas
                JsonArray hyphaArray = obj.getAsJsonArray("hyphas");
                for (JsonElement hyphaElem : hyphaArray) {
                    String hyphaName = hyphaElem.getAsString();
                    Hypha h = (Hypha) registry.getByName(hyphaName);
                    if (h != null) {
                        f.getHyphas().add(h);
                    }
                }

                // Spores
                JsonArray sporeArray = obj.getAsJsonArray("spores");
                for (JsonElement sporeElem : sporeArray) {
                    JsonObject sporeObj = sporeElem.getAsJsonObject();
                    Spore spore = SporeDeserializer.deserialize(sporeObj, registry);
                    if (spore != null) {
                        f.getSpores().add(spore);
                    }
                }

                // Tecton keresés és hozzárendelés
                if (obj.has("tecton")) {
                    String tectonName = obj.get("tecton").getAsString();
                    Tecton tecton = (Tecton) registry.getByName(tectonName);
                    if (tecton != null) {
                        f.setTecton(tecton);
                        tecton.setFungus(f);
                    }
                }
            }
        }

        // Insect
        JsonArray insects = json.getAsJsonArray("insects");
        for (JsonElement elem : insects) {
            JsonObject obj = elem.getAsJsonObject();
            Entomologist e = (Entomologist) registry.getByName(obj.get("entomologist").getAsString());
            Tecton t = (Tecton) registry.getByName(obj.get("currentTecton").getAsString());

            Insect insect = new Insect(e, t);

            // Spore effektek (egyszerűen név alapján)
            JsonArray effects = obj.getAsJsonArray("effects");
            for (JsonElement eff : effects) {
                Spore spore = (Spore) registry.getByName(eff.getAsString());
                insect.getEffects().add(spore);
            }

            e.addInsects(insect);
            t.addInsect(insect);
            namer.register(obj.get("name").getAsString(), insect);
        }

        // 7. GameState visszatöltés
        JsonObject gameState = json.getAsJsonObject("gameState");
        game.settotalRounds(gameState.get("totalRounds").getAsInt());
        game.setCurrentTurn(gameState.get("round").getAsInt());
        JsonElement currentPlayerElem = gameState.get("currentPlayer");
        if (currentPlayerElem != null && !currentPlayerElem.isJsonNull()) {
            String currentPlayerName = currentPlayerElem.getAsString();
            game.setCurrentPlayer((Player) registry.getByName(currentPlayerName));
        } else {
            game.setCurrentPlayer(null);
        }
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
                if (t instanceof ShortHyphaTecton)
                    ((ShortHyphaTecton) t).setHyphaLimit(limit);
                if (t instanceof SingleHyphaTecton)
                    ((SingleHyphaTecton) t).setHyphaLimit(limit);
                if (t instanceof KeepHyphaTecton)
                    ((KeepHyphaTecton) t).setHyphaLimit(limit);
            }
        }
        if (t instanceof ShortHyphaTecton || t instanceof KeepHyphaTecton) {
            if (obj.has("hyphaLifespan")) {
                int lifespan = obj.get("hyphaLifespan").getAsInt();
                if (t instanceof ShortHyphaTecton)
                    ((ShortHyphaTecton) t).setHyphaLifespan(lifespan);
                if (t instanceof KeepHyphaTecton)
                    ((KeepHyphaTecton) t).setHyphaLifespan(lifespan);
            }
        }

        List<Hexagon> hexagons = new ArrayList<>();
        for (JsonElement hexElem : obj.getAsJsonArray("hexagons")) {
            int id = hexElem.getAsInt();
            hexagons.add(new Hexagon(id));
        }
        t.hexagons = hexagons;

        // Speciális attribútumok CSAK ha az adott leszármazott tudja kezelni
        if (t instanceof ShortHyphaTecton && obj.has("hyphaLifespan")) {
            ((ShortHyphaTecton) t).setHyphaLifespan(obj.get("hyphaLifespan").getAsInt());
        }
        if (t instanceof KeepHyphaTecton && obj.has("hyphaLifespan")) {
            ((KeepHyphaTecton) t).setHyphaLifespan(obj.get("hyphaLifespan").getAsInt());
        }
        if (t instanceof SingleHyphaTecton && obj.has("hyphaLimit")) {
            ((SingleHyphaTecton) t).setHyphaLimit(obj.get("hyphaLimit").getAsInt());
        }
        if (t instanceof KeepHyphaTecton && obj.has("hyphaLimit")) {
            ((KeepHyphaTecton) t).setHyphaLimit(obj.get("hyphaLimit").getAsInt());
        }
        if (t instanceof ShortHyphaTecton && obj.has("hyphaLimit")) {
            ((ShortHyphaTecton) t).setHyphaLimit(obj.get("hyphaLimit").getAsInt());
        }

        return t;
    }

}
