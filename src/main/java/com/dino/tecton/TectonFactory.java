package com.dino.tecton;

import java.util.ArrayList;
import java.util.List;

import com.dino.core.Hexagon;
import com.dino.engine.Game;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A Tecton típusú objektumok deszerializálására szolgáló gyármetódus.
 */
public class TectonFactory {

    public static Tecton fromJson(JsonObject obj, Game game) {
        String type = obj.get("type").getAsString();
        Tecton t;

        switch (type) {
            case "SingleHyphaTecton":
                t = new SingleHyphaTecton();
                break;
            case "NoFungiTecton":
                t = new NoFungiTecton();
                break;
            case "ShortHyphaTecton":
                t = new ShortHyphaTecton();
                break;
            case "KeepHyphaTecton":
                t = new KeepHyphaTecton();
                break;
            case "InfiniteHyphaTecton":
                t = new InfiniteHyphaTecton();
                break;
            default:
                throw new IllegalArgumentException("Ismeretlen tecton típus: " + type);
        }

        // Alap attribútumok betöltése
        t.breakChance = obj.get("breakChance").getAsDouble();
        t.breakCount = obj.get("breakCount").getAsInt();

        // Hexagon/neighbours/hypha/fungus/insect/spore kapcsolatok később kerülnek
        // feloldásra,
        // amikor már minden entitás regisztrálva van (ez a második fázisban történik).
        if (obj.has("hexagons")) {
            List<Hexagon> hexList = new ArrayList<>();
            for (JsonElement hexElem : obj.getAsJsonArray("hexagons")) {
                int hexId = hexElem.getAsInt();
                Hexagon hex = game.getBoard().getHexagonById(hexId); // ezt meg kell írni
                if (hex != null)
                    hexList.add(hex);
            }
            t.hexagons = hexList;
        }

        /// Tecton visszatöltése
        if (obj.has("neighbours")) {
            List<Tecton> neighborList = new ArrayList<>();
            for (JsonElement neighborIdElem : obj.getAsJsonArray("neighbours")) {
                String neighborId = neighborIdElem.getAsString();
                Object neighborObj = game.getRegistry().getByName(neighborId);
                if (neighborObj instanceof Tecton) {
                    neighborList.add((Tecton) neighborObj);
                }
            }
            t.setNeighbours(neighborList);
        }

        return t;
    }
}
