package me.lst.recordplus;

import org.bukkit.entity.Entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class EntityTracker implements Serializable {
    private static final long serialVersionUID = 1667L;

    public int entityId;
    public String name;
    public String type;
    public boolean isLiving;
    public String skin;
    
    public transient UUID entityUUID;
    public Map<Integer, List<String>> ticks;

    public transient Recording recording;
    public transient boolean recordNearby;

    public transient Object datawatcher; // NMS, don't specify class
    public transient String lastMoved;
    public transient int lastFireTicks;
    public transient List lastItems; // NMS, don't specify parameters
    public transient byte lastUsing;
    public transient int lastArrows;
    public transient Collection<Entity> lastNearby;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.entityId);
        out.writeUTF(this.name);
        out.writeUTF(this.type);
        out.writeBoolean(this.isLiving);
        out.writeUTF(this.skin);

        out.writeInt(this.ticks.size());

        for (Map.Entry<Integer, List<String>> entry : this.ticks.entrySet()) {
            out.writeInt(entry.getKey());

            List<String> actions = entry.getValue();

            out.writeInt(actions.size());

            for (String action : actions) {
                out.writeUTF(action);
            }
        }
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        this.entityId = in.readInt();
        this.name = in.readUTF();
        this.type = in.readUTF();
        this.isLiving = in.readBoolean();
        this.skin = in.readUTF();

        int size;

        size = in.readInt();
        this.ticks = new HashMap<>(size);

        for (int i = 0; i < size; ++i) {
            int tick = in.readInt();

            int actionAmount = in.readInt();
            List<String> actions = new ArrayList<>(actionAmount);

            for (int j = 0; j < actionAmount; ++j) {
                actions.add(in.readUTF());
            }
            this.ticks.put(tick, actions);
        }
        // Temp/Replay Purposes
        this.entityUUID = UUID.randomUUID();
    }
}
