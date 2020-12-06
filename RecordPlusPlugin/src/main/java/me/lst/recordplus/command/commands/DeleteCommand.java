package me.lst.recordplus.command.commands;

import me.lst.recordplus.RecordingStorage;
import me.lst.recordplus.util.StringUtils;
import me.lst.recordplus.util.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DeleteCommand extends Command {
    RecordingStorage storage;

    public DeleteCommand(RecordingStorage storage) {
        super(Arrays.asList("delete", "del", "remove", "rem"), "commands.record.delete", "<player> <id>", "Delete a recording of a player");
        this.storage = storage;
    }

    @Override
    public void run(CommandSender sender, String parentLabel, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtils.format("&cYou must be a player to execute this command!"));
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

        if (!player.hasPlayedBefore()) {
            sender.sendMessage(StringUtils.format("&cPlayer '%s' was not found!", args[0]));
            return;
        }
        String id = args[1].toLowerCase();

        this.storage.delete(player.getUniqueId(), recording -> id.equals(recording.id), deleted -> sender.sendMessage(StringUtils.format("&6Deleted &e%s &6recording(s)!", deleted)));
    }
}