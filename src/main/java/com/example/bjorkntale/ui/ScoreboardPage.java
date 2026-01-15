package com.example.bjorkntale.ui;

import com.example.bjorkntale.KillScoreboard;
import com.hypixel.hytale.protocol.packets.interface_.CustomPage;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUICommand;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreboardPage {

    public static CustomPage buildKillStatsPage(@Nonnull KillScoreboard scoreboard,
                                                @Nonnull String playerUuid) {

        // Get player's kill stats
        Map<String, Integer> kills = scoreboard.getPlayerKills(playerUuid);

        // Sort by kill count (descending)
        List<Map.Entry<String, Integer>> sortedKills = kills.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());

        // Build UI commands - we need to see CustomUICommand structure to populate this properly
        List<CustomUICommand> commands = new ArrayList<>();

        // For now, create an empty array since we don't know CustomUICommand's structure yet
        // commands.add(createTextCommand("§l§6Kill Statistics§r", 0, 10));
        // ... etc

        // Create the page
        CustomPage page = new CustomPage();
        page.key = "killstats"; // Must not be null!
        page.isInitial = true;
        page.clear = true;
        page.lifetime = CustomPageLifetime.CanDismiss;
        page.commands = commands.toArray(new CustomUICommand[0]); // Empty array for now
        page.eventBindings = null;

        return page;
    }

    private static String formatEntityName(String entityType) {
        if (entityType == null || entityType.isEmpty()) {
            return "Unknown";
        }

        String[] words = entityType.split("_");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase());
        }

        return formatted.toString();
    }
}