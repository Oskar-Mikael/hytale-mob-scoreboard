package com.oskarmikael.mobkilltracker.events;

import com.oskarmikael.mobkilltracker.KillScoreboard;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.query.Query;
import com.oskarmikael.mobkilltracker.MobKillTracker;

import javax.annotation.Nonnull;

public class KillTrackerSystem extends DeathSystems.OnDeathSystem {
    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        // Track all entity deaths
        return Archetype.empty();
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref,
                                 @Nonnull DeathComponent component,
                                 @Nonnull Store<EntityStore> store,
                                 @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        Damage deathInfo = component.getDeathInfo();
        if (deathInfo == null) {
            return;
        }

        // Check if killed by a player
        Damage.Source source = deathInfo.getSource();
        if (source instanceof Damage.EntitySource entitySource) {
            Ref<EntityStore> killerRef = entitySource.getRef();

            if (!killerRef.isValid()) {
                return;
            }

            Player killerPlayer = store.getComponent(killerRef, Player.getComponentType());
            if (killerPlayer != null) {
                PlayerRef playerRef = store.getComponent(killerRef, PlayerRef.getComponentType());
                // Get entity type from victim
                String entityType = getEntityType(ref, store);
                assert playerRef != null;
                String playerUuid = playerRef.getUuid().toString();

                KillScoreboard scoreboard = MobKillTracker.getInstance().getScoreboard();
                scoreboard.incrementKill(playerUuid, entityType);

                int totalKills = scoreboard.getKillCount(playerUuid, entityType);

                if (totalKills % 10 == 0) {
                    killerPlayer.sendMessage(
                            Message.raw("You've killed " + totalKills + " " + entityType.replace('_', ' ') + "(s)!")
                    );
                }
            }
        }
    }

    private String getEntityType(Ref<EntityStore> ref, Store<EntityStore> store) {
        ModelComponent modelComponent = store.getComponent(ref, ModelComponent.getComponentType());
        if (modelComponent != null) {
            Model model = modelComponent.getModel();
            if (model != null) {
                return model.getModelAssetId();
            }
        }

        return "Unknown";
    }
}