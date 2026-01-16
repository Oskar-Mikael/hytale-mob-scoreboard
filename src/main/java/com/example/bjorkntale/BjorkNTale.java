package com.example.bjorkntale;

import com.example.bjorkntale.commands.ScoreboardCommand;
import com.example.bjorkntale.events.KillTracker;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main plugin class.
 * <p>
 * TODO: Implement your plugin logic here.
 *
 * @author YourName
 * @version 1.0.0
 */
public class BjorkNTale extends JavaPlugin {

    private static BjorkNTale instance;

    private final KillScoreboard scoreboard;

    public BjorkNTale(@NotNull JavaPluginInit init) {
        super(init);

        Path dataDirectory = Paths.get("mods", "KillTracker");
        this.scoreboard = new KillScoreboard(dataDirectory);
    }

    /**
     * Get plugin instance.
     */
    @Override
    protected void setup() {
        super.setup();
        this.getCommandRegistry().registerCommand(new ScoreboardCommand(this.scoreboard));
        this.getEntityStoreRegistry().registerSystem(new KillTracker(this.scoreboard));
    }

    public KillScoreboard getScoreboard() {
        return scoreboard;
    }

    public static BjorkNTale getInstance() {
        return instance;
    }
}
