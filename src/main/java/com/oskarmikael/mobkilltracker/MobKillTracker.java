package com.oskarmikael.mobkilltracker;

import com.oskarmikael.mobkilltracker.commands.ScoreboardCommand;
import com.oskarmikael.mobkilltracker.events.KillTrackerSystem;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main plugin class.
 *
 * @author Oskar-Mikael
 * @version 0.1.0
 */
public class MobKillTracker extends JavaPlugin {

    private static MobKillTracker instance;

    private final KillScoreboard scoreboard;

    public MobKillTracker(@NotNull JavaPluginInit init) {
        super(init);

        Path dataDirectory = Paths.get("mods", "MobKillTracker");
        this.scoreboard = new KillScoreboard(dataDirectory);
    }

    /**
     * Get plugin instance.
     */
    @Override
    protected void setup() {
        super.setup();
        this.getCommandRegistry().registerCommand(new ScoreboardCommand());
        this.getEntityStoreRegistry().registerSystem(new KillTrackerSystem());
    }

    public KillScoreboard getScoreboard() {
        return scoreboard;
    }

    public static MobKillTracker getInstance() {
        return instance;
    }
}
