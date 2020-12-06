package me.lst.recordplus.command.commands;

import me.lst.recordplus.RecordPlusPlugin;
import me.lst.recordplus.util.StringUtils;
import me.lst.recordplus.util.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class InformationCommand extends Command {
    public InformationCommand() {
        super(Arrays.asList("info", "version", "ver", "author"), null, null, "Show plugin information");
    }

    @Override
    public void run(CommandSender sender, String parentLabel, String label, String[] args) {
        sender.sendMessage(StringUtils.format("&fThis server is running &9Record&c+ &7v%s &fby &eLST&f!", RecordPlusPlugin.VERSION));
    }
}
