package me.lst.recordplus.command.commands;

import me.lst.recordplus.Recorder;
import me.lst.recordplus.RecordingStorage;
import me.lst.recordplus.util.StringUtils;
import me.lst.recordplus.util.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PlayCommand extends Command {
    Plugin plugin;
    RecordingStorage storage;
    Recorder recorder;

    public PlayCommand(Plugin plugin, RecordingStorage storage, Recorder recorder) {
        super(Arrays.asList("play"), "commands.record.play", "<player> <id>", "Play recording of a player");
        this.plugin = plugin;
        this.storage = storage;
        this.recorder = recorder;
    }

    @Override
    public void run(CommandSender sender, String parentLabel, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtils.format("&cYou must be a player to execute this command!"));
            return;
        }
        Player viewer = (Player) sender;

        if (this.recorder.getViewers().contains(viewer)) {
            sender.sendMessage(StringUtils.format("&cYou are already viewing a recording!"));
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

        if (!player.hasPlayedBefore()) {
            sender.sendMessage(StringUtils.format("&cPlayer '%s' was not found!", args[0]));
            return;
        }
        String id = args[1].toLowerCase();
        String name = player.getName();

        this.storage.load(player.getUniqueId(), recording -> id.equals(recording.id), recordings -> {
            if (recordings.isEmpty()) {
                sender.sendMessage(StringUtils.format("&cRecording '%s' for player '%s' was not found!", args[1], name));
                return;
            }
            Bukkit.getScheduler().runTask(this.plugin, () -> this.recorder.playRecording(viewer, recordings.get(0)));
        });
    }
}