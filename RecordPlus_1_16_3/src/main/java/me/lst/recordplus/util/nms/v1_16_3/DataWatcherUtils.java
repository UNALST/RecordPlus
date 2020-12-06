package me.lst.recordplus.util.nms.v1_16_3;

import me.lst.recordplus.objects.ActionData;
import me.lst.recordplus.util.ReflectionUtils;
import net.minecraft.server.v1_16_R2.DataWatcher;
import net.minecraft.server.v1_16_R2.DataWatcherObject;
import net.minecraft.server.v1_16_R2.DataWatcherRegistry;
import net.minecraft.server.v1_16_R2.EntityPose;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DataWatcherUtils {
    public static final DataWatcherObject<Byte> ENTITY_DESCRIPTION = new DataWatcherObject<>(0, DataWatcherRegistry.a);
    public static final DataWatcherObject<Byte> SKIN_SHOWN = new DataWatcherObject<>(16, DataWatcherRegistry.a);
    public static final DataWatcherObject<EntityPose> POSE = new DataWatcherObject<>(6, DataWatcherRegistry.s);
    public static final DataWatcherObject<Boolean> IS_BABY = new DataWatcherObject<>(15, DataWatcherRegistry.i);
    public static final DataWatcherObject<Byte> USING = new DataWatcherObject<>(7, DataWatcherRegistry.a);
    public static final DataWatcherObject<Float> HEALTH = new DataWatcherObject<>(8, DataWatcherRegistry.c);
    public static final DataWatcherObject<Integer> ARROWS_IN_ENTITY = new DataWatcherObject<>(11, DataWatcherRegistry.b);

    private static Method b = null;

    static {
        try {
            b = DataWatcher.class.getDeclaredMethod("b", DataWatcherObject.class);
            b.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void setFlag(DataWatcher watcher, int i, boolean flag) {
        byte b0 = watcher.get(ENTITY_DESCRIPTION);

        if (flag) {
            set(watcher, ENTITY_DESCRIPTION, (byte) (b0 | 1 << i));
        } else {
            set(watcher, ENTITY_DESCRIPTION, (byte) (b0 & ~(1 << i)));
        }
    }

    public static void setFlag(ActionData actionData, int i, boolean flag) {
        setFlag((DataWatcher) actionData.tracker.datawatcher, i, flag);
    }

    public static <T> void set(DataWatcher watcher, DataWatcherObject<T> object, T value) {
        try {
            DataWatcher.Item<T> datawatcher_item = (DataWatcher.Item<T>) b.invoke(watcher, object);

            if (ObjectUtils.notEqual(value, datawatcher_item.b())) {
                datawatcher_item.a((T) value);
                datawatcher_item.a(true);
                ReflectionUtils.set(DataWatcher.class, "g", watcher, true);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static <T> void set(ActionData actionData, DataWatcherObject<T> object, T value) {
        set((DataWatcher) actionData.tracker.datawatcher, object, value);
    }
}
