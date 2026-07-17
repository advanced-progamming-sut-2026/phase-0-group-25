package Model.PlantsAndZombies;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.File;
import java.util.*;


import java.util.*;

public class GameDataLoader {
    private static Map<String, List<PlantStats>> plantRegistry = new HashMap<>();

    public static void loadGameData() {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(JsonReadFeature.ALLOW_MISSING_VALUES)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        Map<String, List<PlantStats>> rawData = mapper.readValue(
                new File("config/plants_config.json"),
                new TypeReference<Map<String, List<PlantStats>>>() {
                });

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

    public static PlantStats getStatsForPlantLevel(String plantName, int level) {
        List<PlantStats> levels = plantRegistry.get(plantName);
        if (levels != null && level >= 1 && level <= levels.size()) {
            return levels.get(level - 1);
        }
        return null;
    }
}
