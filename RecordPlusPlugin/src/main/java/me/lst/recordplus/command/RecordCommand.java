package me.lst.recordplus.command;

import me.lst.recordplus.RecordPlusPlugin;
import me.lst.recordplus.Recorder;
import me.lst.recordplus.RecordingStorage;
import me.lst.recordplus.command.commands.*;
import me.lst.recordplus.util.command.CommandManager;

public class RecordCommand extends CommandManager {
    public RecordCommand(RecordPlusPlugin plugin, RecordingStorage storage, Recorder recorder) {
        this.register(
                new InformationCommand(),
                new ReloadCommand(plugin),
                new StartCommand(recorder),
                new StopCommand(recorder),
                new ShowCommand(storage),
                new PlayCommand(plugin, storage, recorder),
                new DeleteCommand(storage)
        );
    }
}
