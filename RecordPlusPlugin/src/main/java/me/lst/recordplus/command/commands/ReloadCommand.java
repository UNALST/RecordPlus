package me.lst.recordplus.command.commands;

import me.lst.recordplus.RecordPlusPlugin;
import me.lst.recordplus.util.StringUtils;
import me.lst.recordplus.util.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ReloadCommand extends Command {
    RecordPlusPlugin plugin;

    public ReloadCommand(RecordPlusPlugin plugin) {
        super(Arrays.asList("reload", "rel"), "commands.record.reload", null, "Reload plugin configuration");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String parentLabel, String label, String[] args) {
        this.plugin.getConfiguration().reload();
        sender.sendMessage(StringUtils.format("&aSuccessfully reloaded plugin configuration!"));
    }
}
