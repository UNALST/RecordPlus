package me.lst.recordplus.util.command;

import me.lst.recordplus.util.StringUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommandManager implements CommandExecutor {
    private List<Command> commands = new ArrayList<>();
    private Command defaultCommand = null;

    public CommandManager() {
        this.register(new CommandHelp(this), true);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command bukkitCommand, String label, String[] args) {
        if (args.length == 0) {
            this.defaultCommand.run(sender, label, this.defaultCommand.getDefaultLabel(), args);
            return true;
        }
        String subCommand = args[0].toLowerCase();

        for (Command command : this.commands) {
            if (!command.getLabels().contains(subCommand)) {
                continue;
            }
            if (command.hasPermission(sender)) {
                String usage = command.getUsage();

                if (usage != null && !usage.startsWith("[") && args.length - 1 < usage.split(" ").length) {
                    sender.sendMessage(StringUtils.format("&cInvalid usage! Correct usage: /%s %s %s", label, subCommand, usage));
                    return false;
                }
                command.run(sender, label, subCommand, nextArgs(args));
                return true;
            } else {
                sender.sendMessage(StringUtils.format("&cYou do not have permission to execute this command!"));
                return false;
            }
        }
        this.defaultCommand.run(sender, label, this.defaultCommand.getDefaultLabel(), args);
        return true;
    }

    public void register(Command command, boolean isDefault) {
        this.commands.add(command);

        if (isDefault) {
            this.defaultCommand = command;
        }
    }

    public void register(Command command) {
        this.register(command, false);
    }

    public void register(Collection<Command> commands) {
        for (Command command : commands) {
            this.register(command);
        }
    }

    public void register(Command... commands) {
        for (Command command : commands) {
            this.register(command);
        }
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    private String[] nextArgs(String[] args) {
        return args.length <= 1 ? new String[] {} : Arrays.copyOfRange(args, 1, args.length);
    }
}
