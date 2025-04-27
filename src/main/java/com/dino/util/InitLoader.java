package com.dino.util;

import java.io.File;
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
        System.out.println("Fájl betöltési kísérlet: " + filename);
        File file = new File(filename);
        
        if (!file.exists()) {
            throw new IOException("A fájl nem létezik: " + file.getAbsolutePath());
        }
        
        if (!file.canRead()) {
            throw new IOException("A fájl nem olvasható: " + file.getAbsolutePath());
        }
        
        System.out.println("A fájl létezik és olvasható: " + file.getAbsolutePath());
        System.out.println("Fájl mérete: " + file.length() + " bájt");
        
        try {
            JsonObject json = Serializer.loadFromFile(filename);
            
            if (json == null) {
                throw new IOException("A JSON feldolgozása sikertelen");
            }
            
            System.out.println("JSON sikeresen betöltve a fájlból");
            
            // 1. Játék példány létrehozása
            int totalRounds = 10; // Alapértelmezett
            if (json.has("gameState") && json.getAsJsonObject("gameState").has("totalRounds")) {
                totalRounds = json.getAsJsonObject("gameState").get("totalRounds").getAsInt();
            }
            
            Game game = new Game(totalRounds);
            EntityRegistry registry = game.getRegistry();
            ObjectNamer namer = ObjectNamer.getInstance(registry);
            
            System.out.println("Játék példány létrehozva " + totalRounds + " körrel");
            
            // 2. Gombászok betöltése
            if (json.has("mycologists")) {
                System.out.println("Gombászok betöltése...");
                JsonArray mycologists = json.getAsJsonArray("mycologists");
                for (JsonElement elem : mycologists) {
                    JsonObject obj = elem.getAsJsonObject();
                    Mycologist m = new Mycologist();
                    m.score = obj.has("score") ? obj.get("score").getAsInt() : 0;
                    m.remainingActions = obj.has("remainingActions") ? obj.get("remainingActions").getAsInt() : 1;
                    
                    String name = obj.has("name") ? obj.get("name").getAsString() : "mycologist_" + System.currentTimeMillis();
                    namer.register(name, m);
                    game.getPlayers().add(m);
                    System.out.println("Gombász hozzáadva: " + name);
                }
            }
            
            // 3. Rovarászok betöltése
            if (json.has("entomologists")) {
                System.out.println("Rovarászok betöltése...");
                JsonArray entomologists = json.getAsJsonArray("entomologists");
                for (JsonElement elem : entomologists) {
                    JsonObject obj = elem.getAsJsonObject();
                    Entomologist e = new Entomologist();
                    e.score = obj.has("score") ? obj.get("score").getAsInt() : 0;
                    e.remainingActions = obj.has("remainingActions") ? obj.get("remainingActions").getAsInt() : 3;
                    
                    String name = obj.has("name") ? obj.get("name").getAsString() : "entomologist_" + System.currentTimeMillis();
                    namer.register(name, e);
                    game.getPlayers().add(e);
                    System.out.println("Rovarász hozzáadva: " + name);
                }
            }
            
            // 4. Tektonok betöltése
            Map<String, Tecton> tectonMap = new HashMap<>();
    List<JsonObject> tectonObjects = new ArrayList<>();

    if (json.has("tectons")) {
        System.out.println("Tektonok betöltése...");
        JsonArray tectons = json.getAsJsonArray("tectons");

        // 1. kör: csak létrehozás + regisztráció
        for (JsonElement elem : tectons) {
            JsonObject obj = elem.getAsJsonObject();
            Tecton t = createTectonFromJson(obj);

            String name = obj.has("name") ? obj.get("name").getAsString() : obj.get("id").getAsString();
            namer.register(name, t);
            tectonMap.put(name, t);
            game.getBoard().getAllTectons().add(t);

            tectonObjects.add(obj); // elmentjük későbbre
            System.out.println("Tekton hozzáadva: " + name);
        }

        // 2. kör: szomszédok összekapcsolása
        System.out.println("Tekton szomszédok összekapcsolása...");
        for (JsonObject obj : tectonObjects) {
            String name = obj.has("name") ? obj.get("name").getAsString() : obj.get("id").getAsString();
            Tecton t = (Tecton) registry.getByName(name);

            if (obj.has("neighbours")) {
                JsonArray neighbours = obj.getAsJsonArray("neighbours");
                for (JsonElement neighbourElem : neighbours) {
                    String neighbourName = neighbourElem.getAsString();
                    Tecton neighbour = (Tecton) registry.getByName(neighbourName);

                    if (neighbour != null && !t.getNeighbours().contains(neighbour)) {
                        Tecton.connectTectons(t, neighbour);
                        System.out.println("Tektonok összekapcsolva: " + name + " <-> " + neighbourName);
                    } else if (neighbour == null) {
                        System.out.println("Figyelmeztetés: Nem található szomszéd tekton: " + neighbourName);
                    }
                }
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
            if (json.has("insects")) {
                System.out.println("Rovarok betöltése...");
                JsonArray insects = json.getAsJsonArray("insects");
                for (JsonElement elem : insects) {
                    JsonObject obj = elem.getAsJsonObject();
                    
                    // Entomologist megkeresése
                    Entomologist owner = (Entomologist) registry.getByName(obj.get("owner").getAsString());
                    
                    // Tecton megkeresése
                    Tecton currentTecton = (Tecton) registry.getByName(obj.get("currentTecton").getAsString());
                    
                    Insect insect = new Insect(owner, currentTecton);
                    
                    // Esetleges spore effektek
                    if (obj.has("effects")) {
                        JsonArray effects = obj.getAsJsonArray("effects");
                        for (JsonElement effectElem : effects) {
                            String effectName = effectElem.getAsString();
                            Spore spore = (Spore) registry.getByName(effectName);
                            if (spore != null) {
                                insect.getEffects().add(spore);
                            }
                        }
                    }
                    
                    // Entomologist-hez hozzáadni
                    owner.addInsects(insect);
                    // Tecton-hoz hozzáadni
                    currentTecton.addInsect(insect);
                    // Név regisztráció
                    namer.register(obj.get("id").getAsString(), insect);
                    
                    System.out.println("Rovar regisztrálva: " + obj.get("id").getAsString());
                }
            }
            
            
            // 6. Játékállapot beállítása
            if (json.has("gameState")) {
                System.out.println("Játékállapot beállítása...");
                JsonObject gameState = json.getAsJsonObject("gameState");
                
                if (gameState.has("totalRounds")) {
                    game.settotalRounds(gameState.get("totalRounds").getAsInt());
                }
                
                if (gameState.has("round")) {
                    game.setCurrentTurn(gameState.get("round").getAsInt());
                }
                
                if (gameState.has("currentPlayer") && !gameState.get("currentPlayer").isJsonNull()) {
                    String currentPlayerName = gameState.get("currentPlayer").getAsString();
                    Player currentPlayer = (Player) registry.getByName(currentPlayerName);
                    if (currentPlayer != null) {
                        game.setCurrentPlayer(currentPlayer);
                        System.out.println("Aktuális játékos beállítva: " + currentPlayerName);
                    } else {
                        System.out.println("Figyelmeztetés: Nem található aktuális játékos: " + currentPlayerName);
                    }
                }
            }
            
            System.out.println("Játék betöltése kész!");
            return game;
            
        } catch (Exception e) {
            System.err.println("Hiba a játék betöltése közben: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Nem sikerült betölteni a játékot: " + e.getMessage(), e);
        }
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
