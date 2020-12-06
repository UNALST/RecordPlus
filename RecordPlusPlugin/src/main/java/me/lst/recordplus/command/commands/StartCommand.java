package me.lst.recordplus.command.commands;

import me.lst.recordplus.Recorder;
import me.lst.recordplus.util.StringUtils;
import me.lst.recordplus.util.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class StartCommand extends Command {
    Recorder recorder;

    public StartCommand(Recorder recorder) {
        super(Arrays.asList("start"), "commands.record.start", "<player>", "Start recording a player");
        this.recorder = recorder;
    }

    @Override
    public void run(CommandSender sender, String parentLabel, String label, String[] args) {
        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(StringUtils.format("&cPlayer '%s' was not found!", args[0]));
            return;
        }
        if (this.recorder.isRecording(player)) {
            sender.sendMessage(StringUtils.format("&cPlayer '%s' is already being recorded!", player.getName()));
            return;
        }
        this.recorder.startRecording(player);
        sender.sendMessage(StringUtils.format("&6Started recording player &e%s&6!", player.getName()));
    }
}