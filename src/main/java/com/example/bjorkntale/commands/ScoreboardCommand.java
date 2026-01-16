package com.example.bjorkntale.commands;

import com.example.bjorkntale.KillScoreboard;
import com.example.bjorkntale.ui.ScoreboardPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPage;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
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
import java.util.concurrent.CompletableFuture;
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

        player.getPageManager().openCustomPage(playerRef, store, new ScoreboardPage(playerRefComponent, CustomPageLifetime.CanDismissOrCloseThroughInteraction, this.scoreboard));
    }
}