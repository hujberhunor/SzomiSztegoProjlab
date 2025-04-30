package com.dino.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dino.core.Fungus;
import com.dino.core.Hypha;
import com.dino.core.Insect;
import com.dino.core.Spore;
import com.dino.player.Entomologist;
import com.dino.player.Mycologist;
import com.dino.tecton.Tecton;

public class ObjectNamer {
    private static ObjectNamer instance;

    private final EntityRegistry registry = EntityRegistry.getInstance();
    private final Map<Class<?>, Integer> counters = new HashMap<>();

    private ObjectNamer() {
    }

    public static ObjectNamer getInstance() {
        if (instance == null) {
            instance = new ObjectNamer();
        }
        return instance;
    }

    public static void reset() {
        instance = new ObjectNamer();
    }

    public void register(Object obj) {
        if (registry.isRegistered(obj))
            return;
        String name = generateName(obj);
        registry.register(name, obj);
    }

    public void register(String name, Object obj) {
        if (registry.isRegistered(obj))
            return;
        registry.register(name, obj);
        updateCounterFromName(name, obj.getClass());
    }

    public String getName(Object obj) {
        return registry.getNameOf(obj);
    }

    private String generateName(Object obj) {
        if (obj instanceof Tecton) {
            Tecton tecton = (Tecton) obj;
            List<String> hexIds = tecton.getHexagons().stream()
                    .filter(h -> h != null)
                    .map(h -> String.valueOf(h.getId()))
                    .sorted()
                    .collect(Collectors.toList());

            if (hexIds.isEmpty()) {
                return "tecton_" + nextIndex(Tecton.class);
            } else {
                return "tecton_" + String.join("_", hexIds);
            }
        }

        if (obj instanceof Hypha) {
            Hypha hypha = (Hypha) obj;
            List<String> tectonNames = hypha.getTectons().stream()
                    .map(registry::getNameOf)
                    .collect(Collectors.toList());
            return "hypha_" + String.join("→", tectonNames);
        }

        if (obj instanceof Insect)
            return "insect_" + nextIndex(Insect.class);
        if (obj instanceof Fungus)
            return "fungus_" + nextIndex(Fungus.class);
        if (obj instanceof Spore)
            return "spore_" + nextIndex(Spore.class);
        if (obj instanceof Entomologist)
            return "entomologist_" + nextIndex(Entomologist.class);
        if (obj instanceof Mycologist)
            return "mycologist_" + nextIndex(Mycologist.class);

        throw new IllegalArgumentException("Nem tudok nevet generálni: " + obj.getClass().getSimpleName());
    }

    private int nextIndex(Class<?> clazz) {
        int index = counters.getOrDefault(clazz, 0);
        counters.put(clazz, index + 1);
        return index;
    }

    private void updateCounterFromName(String name, Class<?> clazz) {
        String prefix = clazz.getSimpleName().toLowerCase() + "_";
        if (name.startsWith(prefix)) {
            try {
                int index = Integer.parseInt(name.substring(prefix.length()));
                int current = counters.getOrDefault(clazz, 0);
                if (index >= current)
                    counters.put(clazz, index + 1);
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public Object getByName(String name) {
        return registry.getByName(name);
    }

}
