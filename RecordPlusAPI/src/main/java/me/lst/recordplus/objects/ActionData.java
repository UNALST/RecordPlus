package me.lst.recordplus.objects;

import me.lst.recordplus.EntityTracker;
import org.bukkit.entity.Player;

public class ActionData {
    public final EntityTracker tracker;
    public final String action;
    public final String[] data;
    public final Player viewer;
    public final MutableBoolean sendMetadata;

    public ActionData(EntityTracker tracker, String action, String[] data, Player viewer, MutableBoolean sendMetadata) {
        this.tracker = tracker;
        this.action = action;
        this.data = data;
        this.viewer = viewer;
        this.sendMetadata = sendMetadata;
    }
}
