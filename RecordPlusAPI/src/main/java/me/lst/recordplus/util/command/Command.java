package me.lst.recordplus.util.command;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class Command {
    private List<String> labels;
    private String permission;
    private String usage;
    private String description;

    public Command(List<String> labels, String permission, String usage, String description) {
        this.labels = labels;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
    }

    public Command(List<String> labels) {
        this(labels, null, null, null);
    }

    public abstract void run(CommandSender sender, String parentLabel, String label, String[] args);

    public boolean hasPermission(CommandSender sender) {
        return this.permission == null || sender.hasPermission(this.permission);
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public String getDefaultLabel() {
        return this.getLabels().get(0);
    }

    public String getPermission() {
        return this.permission;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getDescription() {
        return this.description;
    }
}