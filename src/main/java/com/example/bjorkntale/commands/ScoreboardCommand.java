package com.example.bjorkntale.commands;

import com.example.bjorkntale.KillScoreboard;
import com.example.bjorkntale.ui.ScoreboardPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPage;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreboardCommand extends AbstractPlayerCommand {

    private final KillScoreboard scoreboard;

    public ScoreboardCommand(KillScoreboard scoreboard) {
        super("scoreboard", "View your kills scoreboard");
        this.scoreboard = scoreboard;
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> playerRef, @Nonnull PlayerRef playerRefComponent, @Nonnull World world) {

        Player player = store.getComponent(playerRef, Player.getComponentType());
        if (player == null) {
            context.sendMessage(Message.raw("§cError: Could not retrieve player data"));
            return;
        }

        String playerUuid = playerRefComponent.getUuid().toString();
        showKillStatsInChat(context, player, playerUuid);


        // Default: Open GUI
//        openKillStatsGUI(playerRefComponent, playerUuid);
    }

    private void openKillStatsGUI(@Nonnull PlayerRef playerRef, @Nonnull String playerUuid) {

        // Build the custom page
        CustomPage page = ScoreboardPage.buildKillStatsPage(scoreboard, playerUuid);

        // Send the page to the player
        playerRef.getPacketHandler().write(page);
    }

    private void showKillStatsInChat(@Nonnull CommandContext context, @Nonnull Player player, @Nonnull String playerUuid) {

        Map<String, Integer> kills = scoreboard.getPlayerKills(playerUuid);

        if (kills.isEmpty()) {
            context.sendMessage(Message.raw("§7You haven't killed anything yet!"));
            return;
        }

        int totalKills = kills.values().stream().mapToInt(Integer::intValue).sum();

        context.sendMessage(Message.raw("Kill Statistics").bold(true));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("Total Kills: " + totalKills));
        context.sendMessage(Message.raw(""));
        context.sendMessage(Message.raw("Top Kills:"));

        List<Map.Entry<String, Integer>> sortedKills = kills.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).collect(Collectors.toList());

        for (int i = 0; i < Math.min(15, sortedKills.size()); i++) {
            Map.Entry<String, Integer> entry = sortedKills.get(i);
            String rank = String.format("%2d.", i + 1);
            String entityName = formatEntityName(entry.getKey());
            Integer count = entry.getValue();

            context.sendMessage(Message.raw(rank + " " + entityName + " - " + count));
        }

        if (sortedKills.size() > 15) {
            context.sendMessage(Message.raw(""));
            context.sendMessage(Message.raw("... and " + (sortedKills.size() - 15) + " more"));
        }
    }

    private String formatEntityName(String entityType) {
        if (entityType == null || entityType.isEmpty()) {
            return "Unknown";
        }

        String[] words = entityType.split("_");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (formatted.length() > 0) {
                formatted.append(" ");
            }
            formatted.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }

        return formatted.toString();
    }
}