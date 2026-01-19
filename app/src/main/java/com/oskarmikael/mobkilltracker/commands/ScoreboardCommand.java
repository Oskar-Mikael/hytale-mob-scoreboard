package com.oskarmikael.mobkilltracker.commands;

import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.oskarmikael.mobkilltracker.ui.ScoreboardPage;
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

import javax.annotation.Nonnull;

public class ScoreboardCommand extends AbstractPlayerCommand {
    private final OptionalArg<PlayerRef> playerArgument;

    public ScoreboardCommand() {
        super("scoreboard", "View a players scoreboard");
        this.playerArgument = this.withOptionalArg("player", "Player to show scoreboard for", ArgTypes.PLAYER_REF);
    }

    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player commandExecutor = store.getComponent(ref, Player.getComponentType());

        if (commandExecutor == null) {
            context.sendMessage(Message.raw("§cError: Could not retrieve player data"));
            return;
        }

        PlayerRef targetPlayerRef;

        System.out.println(playerArgument);
        if (playerArgument.get(context) == null) {
            targetPlayerRef = playerRef;
        } else {
            targetPlayerRef = context.get(playerArgument);
        }
        System.out.println(targetPlayerRef);

        if (!targetPlayerRef.isValid()) {
            context.sendMessage(Message.raw("§cError: Could not find player"));
            return;
        }

        // Open the scoreboard page for the target player
        commandExecutor.getPageManager().openCustomPage(
                ref,
                store,
                new ScoreboardPage(targetPlayerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction)
        );
    }
}