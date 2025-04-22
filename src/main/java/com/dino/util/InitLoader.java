package com.dino.util;

import com.dino.engine.Game;
// importáld be az összes entitásosztályt

public class InitLoader {

    public static void loadFromFile(String filename, Game game) throws Exception {
        // 1. Fájl beolvasása (pl. JSON -> object)
        // 2. Entitások létrehozása és beregisztrálása
        // 3. Kapcsolatok felépítése (pl. tecton-neighbours, hypha-k, fungus stb.)
        // 4. Logger és Registry frissítése
        // Példa: ObjectMapper mapper = new ObjectMapper(); // ha Jackson-t használsz
        //        JsonNode root = mapper.readTree(new File(filename));
        //        ... végigmenni rajta és game.getRegistry().register(...)
    }
}
