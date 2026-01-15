package com.example.bjorkntale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class KillScoreboard {

    // Player UUID -> (Entity Type -> Kill Count)
    private static final Logger LOGGER = Logger.getLogger(KillScoreboard.class.getName());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, Map<String, Integer>> killStats;
    private final Path dataFile;



    public KillScoreboard(Path dataDirectory) {
        this.killStats = new ConcurrentHashMap<>();
        this.dataFile = dataDirectory.resolve("scoreboard.json");

        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            LOGGER.severe("Failed to create data directory: " + e.getMessage());
        }

        // Load existing data
        loadData();
    }

    public void incrementKill(String playerUuid, String entityType) {
        killStats.computeIfAbsent(playerUuid, k -> new ConcurrentHashMap<>())
                .merge(entityType, 1, Integer::sum);
        saveData();
    }

    public int getKillCount(String playerUuid, String entityType) {
        return killStats.getOrDefault(playerUuid, new HashMap<>())
                .getOrDefault(entityType, 0);
    }

    public Map<String, Integer> getPlayerKills(String playerUuid) {
        return killStats.getOrDefault(playerUuid, new HashMap<>());
    }

    public Map<String, Map<String, Integer>> getAllKills() {
        return new HashMap<>(killStats);
    }

    public void saveData() {
        try (Writer writer = new FileWriter(dataFile.toFile())) {
            GSON.toJson(killStats, writer);
            LOGGER.info("Kill stats saved successfully");
        } catch (IOException e) {
            LOGGER.severe("Failed to save kill stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load data from JSON file
     */
    private void loadData() {
        if (!Files.exists(dataFile)) {
            LOGGER.info("No existing kill stats file found, starting fresh");
            return;
        }

        try (Reader reader = new FileReader(dataFile.toFile())) {
            Type type = new TypeToken<Map<String, Map<String, Integer>>>(){}.getType();
            Map<String, Map<String, Integer>> loaded = GSON.fromJson(reader, type);

            if (loaded != null) {
                killStats.clear();
                killStats.putAll(loaded);
                LOGGER.info("Loaded kill stats for " + killStats.size() + " players");
            }
        } catch (IOException e) {
            LOGGER.severe("Failed to load kill stats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clear all data (useful for admin commands)
     */
    public void clearAll() {
        killStats.clear();
        saveData();
    }

    /**
     * Clear data for a specific player
     */
    public void clearPlayer(String playerUuid) {
        killStats.remove(playerUuid);
        saveData();
    }
}