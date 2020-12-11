package me.lst.recordplus.nms.v1_16_1;

import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import me.lst.recordplus.*;
import me.lst.recordplus.util.StringUtils;
import me.lst.recordplus.util.nms.v1_16_1.DataWatcherUtils;
import me.lst.recordplus.util.nms.v1_16_1.ItemUtils;
import me.lst.recordplus.util.nms.v1_16_1.PacketUtils;
import me.lst.recordplus.util.nms.v1_16_1.Packets;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftMagicNumbers;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class Recorder_1_16_1 extends Recorder {
    public Recorder_1_16_1(Plugin plugin, Configuration configuration, RecordingStorage storage) {
        super(plugin, configuration, storage);
    }

    @Override
    protected void initHandlers() {
        this.setHandler("SPAWN", actionData -> { // Spawn
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                World world = Bukkit.getWorld(actionData.data[1]);

                if (world == null) {
                    if (this.configuration.isVerbose()) {
                        actionData.viewer.sendMessage(StringUtils.format("World '%s' could not be found for entity '%s' (%s)", actionData.data[1], actionData.tracker.entityId, actionData.tracker.entityUUID));
                        this.plugin.getLogger().warning("World '" + actionData.data[1] + "' could not be found for entity '" + actionData.tracker.entityId + "' (" + actionData.tracker.entityUUID + ")");
                    }
                    return;
                }
                double x = Double.parseDouble(actionData.data[2]);
                double y = Double.parseDouble(actionData.data[3]);
                double z = Double.parseDouble(actionData.data[4]);
                float yaw = Float.parseFloat(actionData.data[5]);
                float pitch = Float.parseFloat(actionData.data[6]);

                if (actionData.tracker.type.equals("PLAYER")) {
                    PacketUtils.send(actionData.viewer, Packets.info(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, actionData.tracker));
                    PacketUtils.send(actionData.viewer, Packets.spawnNamed(actionData.tracker.entityId, actionData.tracker.entityUUID, x, y, z, yaw, pitch));
                } else {
                    Integer typeId = EntityMappings.getTypeId(actionData.tracker.type);

                    if (typeId == null) {
                        if (this.configuration.isVerbose()) {
                            actionData.viewer.sendMessage(StringUtils.format("Could not find Type ID for type '%s'!", actionData.tracker.type));
                            this.plugin.getLogger().warning("Could not find Type ID for type '" + actionData.tracker.type + "'");
                        }
                        return;
                    }
                    if (actionData.tracker.isLiving) {
                        PacketUtils.send(actionData.viewer, Packets.spawnLiving(actionData.tracker.entityId, actionData.tracker.entityUUID, typeId, x, y, z, yaw, pitch));
                    } else {
                        PacketUtils.send(actionData.viewer, Packets.spawn(actionData.tracker.entityId, actionData.tracker.entityUUID, typeId, x, y, z, yaw, pitch));
                    }
                }
                if (actionData.tracker.isLiving) {
                    PacketUtils.send(actionData.viewer, Packets.headRotation(actionData.tracker.entityId, yaw));
                }
                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> PacketUtils.send(actionData.viewer, new PacketPlayOutEntityMetadata(actionData.tracker.entityId, (DataWatcher) actionData.tracker.datawatcher, true)), 1L);
            });
        });
        this.setHandler("M", actionData -> { // Movement
            double x = Double.parseDouble(actionData.data[1]);
            double y = Double.parseDouble(actionData.data[2]);
            double z = Double.parseDouble(actionData.data[3]);
            float yaw = Float.parseFloat(actionData.data[4]);
            float pitch = Float.parseFloat(actionData.data[5]);
            int onGround = Integer.parseInt(actionData.data[6]);

            PacketUtils.send(actionData.viewer, Packets.teleport(actionData.tracker.entityId, x, y, z, yaw, pitch, onGround == 1));
            PacketUtils.send(actionData.viewer, Packets.headRotation(actionData.tracker.entityId, yaw));
        });
        this.setHandler("A", actionData -> PacketUtils.send(actionData.viewer, Packets.animation(actionData.tracker.entityId, Integer.parseInt(actionData.data[1])))); // Arm Animation
        this.setHandler("S", actionData -> { // Sneaking/Crouching
            boolean isSneaking = Integer.parseInt(actionData.data[1]) == 1;
            DataWatcherUtils.set(actionData, DataWatcherUtils.POSE, isSneaking ? EntityPose.CROUCHING : EntityPose.STANDING);
            DataWatcherUtils.setFlag(actionData, 1, isSneaking);
            actionData.sendMetadata.setValue(true);
        });
        this.setHandler("SWM", actionData -> { // Swimming
            boolean isSwimming = Integer.parseInt(actionData.data[1]) == 1;
            DataWatcherUtils.set(actionData, DataWatcherUtils.POSE, isSwimming ? EntityPose.SWIMMING : EntityPose.STANDING);
            DataWatcherUtils.setFlag(actionData, 4, isSwimming);
            actionData.sendMetadata.setValue(true);
        });
        this.setHandler("C", actionData -> { // Combustion/Fire
            DataWatcherUtils.setFlag(actionData, 0, Integer.parseInt(actionData.data[1]) == 1);
            actionData.sendMetadata.setValue(true);
        });
        this.setHandler("I", actionData -> { // Inventory
            String[] itemData = actionData.action.substring(2).split("\t");

            try {
                List<Pair<EnumItemSlot, ItemStack>> items = new ArrayList<>();

                for (String item : itemData) {
                    String[] currentItemData = item.split("\0");

                    NBTTagCompound compound = NBTCompressedStreamTools.a(new ByteArrayInputStream(Base64.getDecoder().decode(currentItemData[1])));
                    int dataVersion = compound.getInt("DataVersion");
                    Dynamic<NBTBase> converted = DataConverterRegistry.a().update(DataConverterTypes.ITEM_STACK, new Dynamic<>(DynamicOpsNBT.a, compound), dataVersion, CraftMagicNumbers.INSTANCE.getDataVersion());
                    items.add(Pair.of(EnumItemSlot.valueOf(currentItemData[0]), ItemStack.a((NBTTagCompound) converted.getValue())));
                }
                PacketUtils.send(actionData.viewer, new PacketPlayOutEntityEquipment(actionData.tracker.entityId, items));
            } catch (IOException ignore) {
                // TODO: figure out a way to cross-version itemstacks?
            }
        });
        this.setHandler("D", actionData -> { // Destroy/Die
            if (actionData.tracker.isLiving) {
                DataWatcherUtils.set(actionData, DataWatcherUtils.HEALTH, 0.0F);
                actionData.sendMetadata.setValue(true);
            } else {
                PacketUtils.send(actionData.viewer, new PacketPlayOutEntityDestroy(actionData.tracker.entityId));
                actionData.sendMetadata.setValue(false);
            }
        });
        this.setHandler("R", actionData -> { // Remove
            PacketUtils.send(actionData.viewer, new PacketPlayOutEntityDestroy(actionData.tracker.entityId));

            if (actionData.tracker.type.equals("PLAYER")) {
                PacketUtils.send(actionData.viewer, Packets.info(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, actionData.tracker));
            }
            actionData.sendMetadata.setValue(false);
        });
        this.setHandler("B", actionData -> { // Ageable Entity isBaby
            DataWatcherUtils.set(actionData, DataWatcherUtils.IS_BABY, true);
            actionData.sendMetadata.setValue(true);
        });
        this.setHandler("U", actionData -> { // Using or not in hand
            DataWatcherUtils.set(actionData, DataWatcherUtils.USING, Byte.parseByte(actionData.data[1]));
            actionData.sendMetadata.setValue(true);
        });
        this.setHandler("AR", actionData -> { // Arrows in entity
            DataWatcherUtils.set(actionData, DataWatcherUtils.ARROWS_IN_ENTITY, Integer.parseInt(actionData.data[1]));
            actionData.sendMetadata.setValue(true);
        });
        this.setHandler("V", actionData -> { // Velocity
            Vec3D mot = new Vec3D(Double.parseDouble(actionData.data[1]), Double.parseDouble(actionData.data[2]), Double.parseDouble(actionData.data[3]));
            PacketUtils.send(actionData.viewer, new PacketPlayOutEntityVelocity(actionData.tracker.entityId, mot));
        });
        this.setHandler("MNT", actionData -> PacketUtils.send(actionData.viewer, Packets.mount(Integer.parseInt(actionData.data[1]), actionData.tracker.entityId))); // Mount
        this.setHandler("UNMNT", actionData -> PacketUtils.send(actionData.viewer, Packets.mount(actionData.tracker.entityId))); // Unmount
        this.setHandler("METADATA", actionData -> PacketUtils.send(actionData.viewer, new PacketPlayOutEntityMetadata(actionData.tracker.entityId, (DataWatcher) actionData.tracker.datawatcher, true))); // Metadata
    }

    @Override
    public void tick(Map<Entity, List<EntityTracker>> trackedEntities) {
        for (Map.Entry<Entity, List<EntityTracker>> entry : trackedEntities.entrySet()) {
            Entity entity = entry.getKey();

            if (entity.isDead()) {
                this.recordAction(entity, "D");
                this.trackedEntities.remove(entity);
                continue;
            }
            if (!(entity instanceof LivingEntity) || entry.getValue().isEmpty()) {
                continue;
            }
            net.minecraft.server.v1_16_R1.EntityLiving nmsEntity = ((CraftLivingEntity) entity).getHandle();

            EntityTracker tracker = entry.getValue().get(0);
            List<Pair<EnumItemSlot, ItemStack>> items = ItemUtils.getItems(nmsEntity);

            if (items.size() != tracker.lastItems.size()) {
                this.recordAction(tracker, "I " + ItemUtils.itemsToString(items));
            } else {
                for (int i = 0; i < items.size(); ++i) {
                    if (items.get(i).getSecond() != ((Pair<EnumItemSlot, ItemStack>) tracker.lastItems.get(i)).getSecond()) {
                        this.recordAction(tracker, "I " + ItemUtils.itemsToString(items));
                        break;
                    }
                }
            }
            tracker.lastItems = items;

            byte using = nmsEntity.getDataWatcher().get(DataWatcherUtils.USING);

            if (using != tracker.lastUsing) {
                this.recordAction(tracker, "U " + using);
            }
            tracker.lastUsing = using;

            int arrows = nmsEntity.getDataWatcher().get(DataWatcherUtils.ARROWS_IN_ENTITY);

            if (arrows != tracker.lastArrows) {
                this.recordAction(tracker, "AR " + arrows);
            }
            tracker.lastArrows = arrows;
        }
        super.tick(trackedEntities);
    }

    @Override
    public void applySkin(Player player, EntityTracker tracker) {
        Iterator<Property> iterator = ((CraftPlayer) player).getHandle().getProfile().getProperties().get("textures").iterator();

        if (iterator.hasNext()) {
            Property property = iterator.next();
            tracker.skin = property.getValue() + '\0' + property.getSignature();
        }
    }

    @Override
    public void initDataWatcher(EntityTracker tracker) {
        DataWatcher watcher = new DataWatcher(null);
        watcher.register(DataWatcherUtils.ENTITY_DESCRIPTION, (byte) 0);
        watcher.register(DataWatcherUtils.POSE, EntityPose.STANDING);

        if (tracker.isLiving) {
            if (tracker.type.equals("PLAYER")) {
                watcher.register(DataWatcherUtils.SKIN_SHOWN, (byte) 0xFF);
            } else {
                watcher.register(DataWatcherUtils.IS_BABY, false);
            }
            watcher.register(DataWatcherUtils.USING, (byte) 0);
            watcher.register(DataWatcherUtils.HEALTH, 20.0F);
            watcher.register(DataWatcherUtils.ARROWS_IN_ENTITY, 0);
        }
        tracker.datawatcher = watcher;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSwim(EntityToggleSwimEvent event) {
        this.recordAction(event.getEntity(), "SWM " + (event.isSwimming() ? 1 : 0));
    }

    @Override
    public void handleStartRecording(Entity entity, Recording recording) {
        if (entity instanceof LivingEntity && ((LivingEntity) entity).isSwimming()) {
            this.recordAction(entity, "SWM 1");
        }
    }
}
