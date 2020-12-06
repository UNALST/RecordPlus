package me.lst.recordplus;

import me.lst.recordplus.command.RecordCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class RecordPlusPlugin extends JavaPlugin {
    public static final String VERSION = "1.0.0";

    private Configuration configuration;
    private RecordingStorage storage;
    private Recorder recorder;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.configuration = new Configuration(this);
        this.configuration.reload();

        this.storage = new RecordingStorage(this, this.configuration);
        this.recorder = this.initRecorder();

        if (this.recorder == null) {
            this.getLogger().warning("Unable to initialize recorder, invalid Bukkit version!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.getServer().getPluginManager().registerEvents(this.recorder, this);

        this.getCommand("record").setExecutor(new RecordCommand(this, this.storage, this.recorder));

        this.initRecorder();
    }

    @Override
    public void onDisable() {

    }

    private Recorder initRecorder() {
        String version = this.getServer().getBukkitVersion().split("-")[0];

        switch (version) {
            case "1.16.4":
                return new me.lst.recordplus.nms.v1_16_4.Recorder_1_16_4(this, this.configuration, this.storage);
            case "1.16.3":
                return new me.lst.recordplus.nms.v1_16_3.Recorder_1_16_3(this, this.configuration, this.storage);
            default:
                return null;
        }
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public Recorder getRecorder() {
        return this.recorder;
    }
}