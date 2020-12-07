package me.lst.recordplus.command.commands;

import me.lst.recordplus.Recording;
import me.lst.recordplus.RecordingStorage;
import me.lst.recordplus.util.IntegerUtils;
import me.lst.recordplus.util.NumberUtils;
import me.lst.recordplus.util.StringUtils;
import me.lst.recordplus.util.command.Command;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;

public class ShowCommand extends Command {
    private static final int PAGE_SIZE = 9;

    RecordingStorage storage;

    public ShowCommand(RecordingStorage storage) {
        super(Arrays.asList("show", "view", "see"), "commands.record.show", "<player>", "Show recordings of a player");
        this.storage = storage;
    }

    @Override
    public void run(CommandSender sender, String parentLabel, String label, String[] args) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

        if (player.getFirstPlayed() == 0) {
            sender.sendMessage(StringUtils.format("&cPlayer '%s' was not found!", args[0]));
            return;
        }
        String name = player.getName();

        this.storage.load(player.getUniqueId(), recordings -> {
            if (args.length < 2) {
                this.showRecordings(sender, name, recordings, 1);
            } else {
                IntegerUtils.IntegerParseResult result = IntegerUtils.parseInt(args[1]);
                this.showRecordings(sender, name, recordings, result.isPositive() ? result.getValue() : 1);
            }
        });
    }

    public void showRecordings(CommandSender sender, String username, List<Recording> recordings, int page) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss MMMM dd, yyyy", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("EST"));

        int size = recordings.size();
        int pages = Math.max(1, (int) Math.ceil((double) size / PAGE_SIZE));

        page = Math.min(page, pages);
        int index = Math.min(page * PAGE_SIZE, size);

        sender.sendMessage(StringUtils.format("&7---- &6%s&e's Recordings &7-- &ePage &c%s&7/&c%s &7----", username, NumberUtils.format(page), NumberUtils.format(pages)));

        for (int i = (page - 1) * PAGE_SIZE; i < index; ++i) {
            Recording recording = recordings.get(i);
            String text = StringUtils.format("%s &7(ID: %s)", format.format(new Date(recording.date)), recording.id);

            if (sender instanceof Player) {
                TextComponent message = new TextComponent(text);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, StringUtils.format("/rec play %s %s", username, recording.id)));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { new TextComponent("Click to play") }));
                ((Player) sender).spigot().sendMessage(message);
            } else {
                sender.sendMessage(text);
            }
        }
    }
}