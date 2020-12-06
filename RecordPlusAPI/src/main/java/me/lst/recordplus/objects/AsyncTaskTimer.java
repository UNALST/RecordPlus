package me.lst.recordplus.objects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class AsyncTaskTimer {
    private Plugin plugin;
    private int id;

    public AsyncTaskTimer(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setTask(Runnable runnable) {
        this.id = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, 0L, 1L).getTaskId();
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.id);
    }
}
