package com.oskarmikael.mobkilltracker;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class MyConfig {
    private String serverName;
    private int maxPlayers = 20;  // Default value
    private boolean debugMode = false;

    public static final BuilderCodec<MyConfig> CODEC = BuilderCodec.builder(MyConfig.class, MyConfig::new)
        .append(new KeyedCodec<>("ServerName", Codec.STRING),
            (config, val) -> config.serverName = val,
            config -> config.serverName)
        .add()
        .append(new KeyedCodec<>("MaxPlayers", Codec.INTEGER),
            (config, val) -> config.maxPlayers = val,
            config -> config.maxPlayers)
        .add()
        .append(new KeyedCodec<>("DebugMode", Codec.BOOLEAN),
            (config, val) -> config.debugMode = val,
            config -> config.debugMode)
        .add()
        .build();

    // Getters...
    public String getServerName() { return serverName; }
    public int getMaxPlayers() { return maxPlayers; }
    public boolean isDebugMode() { return debugMode; }
}
