package me.lst.recordplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// TODO: Add preview support instead of loading all recordings in their entirety to match one
public class RecordingStorage {
    private Plugin plugin;
    private Configuration configuration;

    public RecordingStorage(Plugin plugin, Configuration configuration) {
        this.plugin = plugin;
        this.configuration = configuration; // TODO: Add different storage type options
    }

    public void save(Recording recording) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            File recordings = new File(this.plugin.getDataFolder(), "recordings");

            if (!recordings.exists()) {
                recordings.mkdirs();
            }
            File folder = new File(recordings, recording.targetUUID);

            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(folder, recording.id + ".zip");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            try {
                FileOutputStream fileOut = new FileOutputStream(file);
                GZIPOutputStream gzipOut = new GZIPOutputStream(fileOut);
                ObjectOutputStream out = new ObjectOutputStream(gzipOut);

                out.writeObject(recording);

                out.close();
                gzipOut.close();
                fileOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void load(UUID uuid, Consumer<List<Recording>> consumer) {
        this.load(uuid, recording -> true, consumer);
    }

    public Recording load(File file) {
        try {
            FileInputStream fileIn = new FileInputStream(file);
            GZIPInputStream gzipIn = new GZIPInputStream(fileIn);
            ObjectInputStream in = new ObjectInputStream(gzipIn);

            Recording recording = (Recording) in.readObject();

            in.close();
            gzipIn.close();
            fileIn.close();

            return recording;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void load(UUID uuid, Predicate<Recording> predicate, Consumer<List<Recording>> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            List<Recording> recordings = new ArrayList<>();

            File recordingsDirectory = new File(this.plugin.getDataFolder(), "recordings");

            if (!recordingsDirectory.exists()) {
                consumer.accept(recordings);
                return;
            }
            File folder = new File(recordingsDirectory, uuid.toString());

            if (!folder.exists() || !folder.isDirectory()) {
                consumer.accept(recordings);
                return;
            }
            for (File file : folder.listFiles()) {
                if (!file.getName().endsWith(".zip")) {
                    continue;
                }
                Recording recording = this.load(file);

                if (predicate.test(recording)) {
                    recordings.add(recording);
                }
            }
            recordings.sort(Comparator.comparing(recording -> recording.date));

            consumer.accept(recordings);
        });
    }

    public void delete(UUID uuid, Predicate<Recording> predicate, Consumer<Integer> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            int deleted = 0;

            File recordingsDirectory = new File(this.plugin.getDataFolder(), "recordings");

            if (!recordingsDirectory.exists()) {
                consumer.accept(deleted);
                return;
            }
            File folder = new File(recordingsDirectory, uuid.toString());

            if (!folder.exists() || !folder.isDirectory()) {
                consumer.accept(deleted);
                return;
            }
            for (File file : folder.listFiles()) {
                if (!file.getName().endsWith(".zip")) {
                    continue;
                }
                Recording recording = this.load(file);

                if (predicate.test(recording) && file.delete()) {
                    ++deleted;
                }
            }
            consumer.accept(deleted);
        });
    }
}
