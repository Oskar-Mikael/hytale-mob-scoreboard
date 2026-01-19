package com.oskarmikael.mobkilltracker.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.oskarmikael.mobkilltracker.ui.ScoreboardPage;
import org.jetbrains.annotations.NotNull;

public class LeaderboardCommand extends AbstractPlayerCommand {
    public LeaderboardCommand() {
        super("leaderboard", "Show the mob killing leaderboard");
    }

    @Override
    protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());

        if (player == null) {
            commandContext.sendMessage(Message.raw("§cError: Could not retrieve player data"));
            return;
        }

        player.getPageManager().openCustomPage(ref, store, new ScoreboardPage(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction));
    }
}
