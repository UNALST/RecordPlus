package me.lst.recordplus.util.command;

import me.lst.recordplus.util.IntegerUtils;
import me.lst.recordplus.util.NumberUtils;
import me.lst.recordplus.util.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class CommandHelp extends Command {
    private static final int PAGE_SIZE = 9;

    private CommandManager manager;

    public CommandHelp(CommandManager manager) {
        super(Arrays.asList("help", "h"), null, "[<page>]", "Show command options");
        this.manager = manager;
    }

    @Override
    public void run(CommandSender sender, String parentLabel, String label, String[] args) {
        if (args.length == 0) {
            this.showCommands(sender, parentLabel, 1);
        } else {
            IntegerUtils.IntegerParseResult result = IntegerUtils.parseInt(args[0]);
            this.showCommands(sender, parentLabel, result.isPositive() ? result.getValue() : 1);
        }
    }

    public void showCommands(CommandSender sender, String parentLabel, int page) {
        List<Command> commands = this.manager.getCommands();

        int size = commands.size();
        int pages = Math.max(1, (int) Math.ceil((double) size / PAGE_SIZE));

        page = Math.min(page, pages);
        int index = Math.min(page * PAGE_SIZE, size);

        StringBuilder builder = new StringBuilder();
        builder.append("&7---- &eHelp &7-- &ePage &c").append(NumberUtils.format(page)).append("&7/&c").append(NumberUtils.format(pages)).append(" &7----");

        for (int i = (page - 1) * PAGE_SIZE; i < index; ++i) {
            Command command = commands.get(i);
            builder.append("\n&6/").append(parentLabel).append(' ').append(command.getDefaultLabel());

            if (command.getUsage() != null) {
                builder.append(' ').append(command.getUsage());
            }
            if (command.getDescription() != null) {
                builder.append(" &f").append(command.getDescription());
            }
        }
        sender.sendMessage(StringUtils.format(builder.toString()));
    }
}
