package me.lst.recordplus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recording implements Serializable {
    private static final long serialVersionUID = 1668L;

    public long date;
    public String id;
    public String targetLocation;
    public int tickDuration;

    public List<EntityTracker> entities;

    public transient String targetUUID;
    public transient int tickStarted;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeLong(this.date);
        out.writeUTF(this.id);
        out.writeUTF(this.targetLocation);
        out.writeInt(this.tickDuration);

        out.writeInt(this.entities.size());

        for (EntityTracker tracker : this.entities) {
            out.writeObject(tracker);
        }
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        this.date = in.readLong();
        this.id = in.readUTF();
        this.targetLocation = in.readUTF();
        this.tickDuration = in.readInt();

        int size = in.readInt();
        this.entities = new ArrayList<>(size);

        for (int i = 0; i < size; ++i) {
            this.entities.add((EntityTracker) in.readObject());
        }
    }
}
