package com.example.bjorkntale.ui;

import com.example.bjorkntale.KillScoreboard;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPage;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUICommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreboardPage extends CustomUIPage {

    private final KillScoreboard scoreboard;

    public ScoreboardPage(@NotNull PlayerRef playerRef, @NotNull CustomPageLifetime lifetime, @NotNull KillScoreboard scoreboard) {
        super(playerRef, lifetime);
        this.scoreboard = scoreboard;
    }

    @Override
    public void build(@NotNull Ref<EntityStore> ref, @NotNull UICommandBuilder uiCommandBuilder, @NotNull UIEventBuilder uiEventBuilder, @NotNull Store<EntityStore> store) {
        uiCommandBuilder.append("Scoreboard.ui");
        uiCommandBuilder.set("#MyLabel.TextSpans", Message.raw("Mob Kills Scoreboard"));

        // Get player's kill stats
        Map<String, Integer> kills = scoreboard.getPlayerKills(playerRef.getUuid().toString());

        // Sort by kill count (descending)
        List<Map.Entry<String, Integer>> sortedKills = kills.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .toList();

        for (int i = 0; i < sortedKills.size(); i++) {
            Map.Entry<String, Integer> entry = sortedKills.get(i);
            String itemName = entry.getKey();
            Integer killCount = entry.getValue();

            uiCommandBuilder.appendInline("#IndexCards", "Group { LayoutMode: Left; Anchor: (Bottom: 0); }");


            // Append the UI string to the list container
            uiCommandBuilder.append("#IndexCards[" + i + "]", "ScoreboardItem.ui");

            String itemSelector = "#IndexCards[" + i + "]";
            uiCommandBuilder.set(itemSelector + " #Rank.Text", (i + 1) + ".");
            uiCommandBuilder.set(itemSelector + " #Name.Text", itemName.replace("_", " "));
            uiCommandBuilder.set(itemSelector + " #Kills.Text", killCount + (killCount > 1 ? " kills" : " kill"));
        }
    }
}