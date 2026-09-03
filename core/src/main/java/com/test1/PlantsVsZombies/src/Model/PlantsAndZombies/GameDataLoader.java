package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDataLoader {
    private static final ObjectMapper mapper = JsonMapper.builder()
        .enable(JsonReadFeature.ALLOW_MISSING_VALUES)
        .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
        .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build();
    private static final Map<String, List<PlantStats>> plantRegistry = new HashMap<>();
    private static final Map<String, ZombieStats> zombieRegistry = new HashMap<>();

    public static void loadGameData() {
        loadPlantData();
        loadZombieData();
    }

    private static void loadPlantData() {
        Map<String, List<PlantStats>> rawData = null;
        try {
            rawData = mapper.readValue(
                new File("assets/jsonFiles/plants_config.json"),
                new TypeReference<Map<String, List<PlantStats>>>() {
                });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, List<PlantStats>> entry : rawData.entrySet()) {
            String plantName = entry.getKey();
            List<PlantStats> validatedLevels = new ArrayList<>();

            int levelCounter = 1;
            for (PlantStats stats : entry.getValue()) {
                if (stats == null) {
                    continue;
                }
                stats.setLevel(levelCounter);
                levelCounter += 1;
                validatedLevels.add(stats);
            }
            plantRegistry.put(plantName, validatedLevels);
        }
    }

    private static void loadZombieData() {
        Map<String, ZombieStats> rawData = null;
        try {
            rawData = mapper.readValue(
                new File("assets/jsonFiles/zombies_config.json"),
                new TypeReference<Map<String, ZombieStats>>() {
                }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        zombieRegistry.putAll(rawData);
    }


    public static PlantStats getStatsForPlantLevel(String plantName, int level) {
        List<PlantStats> levels = plantRegistry.get(plantName);
        if (levels != null && level >= 1 && level <= levels.size()) {
            return levels.get(level - 1);
        }
        return null;
    }

    public static ZombieStats getStatsForZombie(String zombieName) {
        return zombieRegistry.get(zombieName);
    }
}
