package com.oskarmikael.mobkilltracker.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.oskarmikael.mobkilltracker.KillScoreboard;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.oskarmikael.mobkilltracker.MobKillTracker;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ScoreboardPage extends InteractiveCustomUIPage<ScoreboardPage.ScoreboardPageCodec> {

    private String sortOrder;
    private String order;

    public ScoreboardPage(@NotNull PlayerRef playerRef, @NotNull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, ScoreboardPageCodec.CODEC);
        this.sortOrder = "count";
        this.order = "desc";
    }

    @Override
    public void build(@NotNull Ref<EntityStore> ref, @NotNull UICommandBuilder uiCommandBuilder, @NotNull UIEventBuilder uiEventBuilder, @NotNull Store<EntityStore> store) {
        uiCommandBuilder.append("Scoreboard.ui");
        uiCommandBuilder.set("#PlayerLabel.TextSpans", Message.raw("Player: " + playerRef.getUsername()));
        uiCommandBuilder.set("#SortLabel.TextSpans", Message.raw("Sort by"));
        uiCommandBuilder.set("#SortCount.TextSpans", Message.raw("Kill count"));
        uiCommandBuilder.set("#SortName.TextSpans", Message.raw("Name"));

        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SortCount",
                EventData.of("Action", "count"),
                false
        );
        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SortName",
                EventData.of("Action", "name"),
                false
        );

        // Get player's kill stats
        KillScoreboard scoreboard = MobKillTracker.getInstance().getScoreboard();
        Map<String, Integer> kills = scoreboard.getPlayerKills(playerRef.getUuid().toString());

        // Sort by kill count or name
        List<Map.Entry<String, Integer>> sortedKills = kills.entrySet()
                .stream()
                .sorted(sortOrder.equals("name")
                        ? (order.equals("asc") ? Map.Entry.comparingByKey() : Map.Entry.<String, Integer>comparingByKey().reversed())
                        : (order.equals("asc") ? Map.Entry.comparingByValue() : Map.Entry.<String, Integer>comparingByValue().reversed()))
                .toList();

        if (sortOrder.equals("count")) {
            uiCommandBuilder.set("#SortCount.TextSpans", Message.raw("> Kill count <")); // Show indicator
            uiCommandBuilder.set("#SortName.TextSpans", Message.raw("Name"));
        } else {
            uiCommandBuilder.set("#SortCount.TextSpans", Message.raw("Kill count"));
            uiCommandBuilder.set("#SortName.TextSpans", Message.raw("> Name <")); // Show indicator
        }

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

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl ScoreboardPageCodec data) {
        super.handleDataEvent(ref, store, data);

        if (data.action != null) {
            if (this.sortOrder.equals(data.action)) {
                this.order = getOrder();
            } else {
                this.sortOrder = data.action;
                this.order = this.sortOrder.equals("name") ? "asc" : "desc";
            }

            // Trigger a UI update to rebuild with the new sort order
            this.sendUpdate();
            this.rebuild();
        }
    }

    private String getOrder() {
        return order = order.equals("desc") ? "asc" : "desc";
    }

    public static class ScoreboardPageCodec {
        static final String KEY_ACTION = "Action";

        public static final BuilderCodec<ScoreboardPageCodec> CODEC =
                BuilderCodec.<ScoreboardPageCodec>builder(ScoreboardPageCodec.class, ScoreboardPageCodec::new)
                        .addField(
                                new KeyedCodec<>(KEY_ACTION, Codec.STRING),
                                (scoreboardData, s) -> scoreboardData.action = s,
                                scoreboardData -> scoreboardData.action
                        )
                        .build();

        private String action;
    }
}