package me.lst.recordplus;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Configuration {
    public static final int SPAWN_DELAY_TICKS = 2;

    private boolean verbose;
    private boolean roundDecimals;
    private boolean recordNearby;
    private double recordNearbyHorizontalDistance;
    private double recordNearbyVerticalDistance;
    private double playbackDistanceRequired;

    private final Plugin plugin;

    public Configuration(Plugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        this.plugin.reloadConfig();

        FileConfiguration configuration = this.plugin.getConfig();
        this.verbose = configuration.getBoolean("verbose", true);
        this.roundDecimals = configuration.getBoolean("round-decimals", true);
        this.recordNearby = configuration.getBoolean("record-nearby", true);
        this.recordNearbyHorizontalDistance = configuration.getDouble("record-nearby-distance-horizontal", 8.0D);
        this.recordNearbyVerticalDistance = configuration.getDouble("record-nearby-distance-vertical", 8.0D);
        this.playbackDistanceRequired = configuration.getDouble("playback-distance-required", 25.0D);
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public boolean getRoundDecimals() {
        return this.roundDecimals;
    }

    public boolean getRecordNearby() {
        return this.recordNearby;
    }

    public double getRecordNearbyHorizontalDistance() {
        return this.recordNearbyHorizontalDistance;
    }

    public double getRecordNearbyVerticalDistance() {
        return this.recordNearbyVerticalDistance;
    }

    public double getPlaybackDistanceRequired() {
        return this.playbackDistanceRequired;
    }
}
