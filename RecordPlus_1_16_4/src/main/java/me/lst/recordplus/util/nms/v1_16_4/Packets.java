package me.lst.recordplus.util.nms.v1_16_4;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.lst.recordplus.EntityTracker;
import me.lst.recordplus.util.ReflectionUtils;
import me.lst.recordplus.util.StringUtils;
import net.minecraft.server.v1_16_R3.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class Packets {
    private static Constructor<?> playerInfoData;

    static {
        try {
            Class<?> info = PacketPlayOutPlayerInfo.class.getClasses()[0];
            playerInfoData = info.getConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static PacketPlayOutNamedEntitySpawn spawnNamed(int id, UUID uuid, double x, double y, double z, float yaw, float pitch) {
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
        ReflectionUtils.set(PacketPlayOutNamedEntitySpawn.class, "a", packet, id);
        ReflectionUtils.set(PacketPlayOutNamedEntitySpawn.class, "b", packet, uuid);
        ReflectionUtils.set(PacketPlayOutNamedEntitySpawn.class, "c", packet, x);
        ReflectionUtils.set(PacketPlayOutNamedEntitySpawn.class, "d", packet, y);
        ReflectionUtils.set(PacketPlayOutNamedEntitySpawn.class, "e", packet, z);
        ReflectionUtils.set(PacketPlayOutNamedEntitySpawn.class, "f", packet, (byte) ((int) (yaw * 256.0F / 360.0F)));
        ReflectionUtils.set(PacketPlayOutNamedEntitySpawn.class, "g", packet, (byte) ((int) (pitch * 256.0F / 360.0F)));
        return packet;
    }

    public static PacketPlayOutPlayerInfo info(PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, EntityTracker tracker) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        ReflectionUtils.set(PacketPlayOutPlayerInfo.class, "a", packet, action);

        GameProfile profile = new GameProfile(tracker.entityUUID, tracker.name);

        if (!tracker.skin.isEmpty() && action == PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER) {
            String[] skin = tracker.skin.split("\0");
            profile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));
        }
        try {
            Object data = playerInfoData.newInstance(packet, profile, 1, EnumGamemode.SURVIVAL, new ChatMessage(StringUtils.random(16)));
            ReflectionUtils.set(PacketPlayOutPlayerInfo.class, "b", packet, Arrays.asList(data));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return packet;
    }

    public static PacketPlayOutSpawnEntityLiving spawnLiving(int id, UUID uuid, int type, double x, double y, double z, float yaw, float pitch) {
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "a", packet, id);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "b", packet, uuid);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "c", packet, type);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "d", packet, x);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "e", packet, y);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "f", packet, z);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "g", packet, 0);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "h", packet, 0);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "i", packet, 0);
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "j", packet, (byte) ((int) (yaw * 256.0F / 360.0F)));
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "k", packet, (byte) ((int) (pitch * 256.0F / 360.0F)));
        ReflectionUtils.set(PacketPlayOutSpawnEntityLiving.class, "l", packet, (byte) ((int) (yaw * 256.0F / 360.0F)));
        return packet;
    }

    public static PacketPlayOutSpawnEntity spawn(int id, UUID uuid, int type, double x, double y, double z, float yaw, float pitch) {
        PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity();
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "a", packet, id);
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "b", packet, uuid);
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "k", packet, IRegistry.ENTITY_TYPE.fromId(type));
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "c", packet, x);
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "d", packet, y);
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "e", packet, z);
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "f", packet, 0);
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "g", packet, 0);
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "h", packet, 0);
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "i", packet, MathHelper.d(yaw * 256.0F / 360.0F));
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "j", packet, MathHelper.d(pitch * 256.0F / 360.0F));
        ReflectionUtils.set(PacketPlayOutSpawnEntity.class, "l", packet, 0);
        return packet;
    }

    public static PacketPlayOutEntityTeleport teleport(int id, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        ReflectionUtils.set(PacketPlayOutEntityTeleport.class, "a", packet, id);
        ReflectionUtils.set(PacketPlayOutEntityTeleport.class, "b", packet, x);
        ReflectionUtils.set(PacketPlayOutEntityTeleport.class, "c", packet, y);
        ReflectionUtils.set(PacketPlayOutEntityTeleport.class, "d", packet, z);
        ReflectionUtils.set(PacketPlayOutEntityTeleport.class, "e", packet, (byte) ((int) (yaw * 256.0F / 360.0F)));
        ReflectionUtils.set(PacketPlayOutEntityTeleport.class, "f", packet, (byte) ((int) (pitch * 256.0F / 360.0F)));
        ReflectionUtils.set(PacketPlayOutEntityTeleport.class, "g", packet, onGround);
        return packet;
    }

    public static PacketPlayOutEntityHeadRotation headRotation(int id, float yaw) {
        PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
        ReflectionUtils.set(PacketPlayOutEntityHeadRotation.class, "a", packet, id);
        ReflectionUtils.set(PacketPlayOutEntityHeadRotation.class, "b", packet, (byte) MathHelper.d(yaw * 256.0F / 360.0F));
        return packet;
    }

    public static PacketPlayOutAnimation animation(int id, int animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        ReflectionUtils.set(PacketPlayOutAnimation.class, "a", packet, id);
        ReflectionUtils.set(PacketPlayOutAnimation.class, "b", packet, animation);
        return packet;
    }

    public static PacketPlayOutMount mount(int id, int... ids) {
        PacketPlayOutMount packet = new PacketPlayOutMount();
        ReflectionUtils.set(PacketPlayOutMount.class, "a", packet, id);
        ReflectionUtils.set(PacketPlayOutMount.class, "b", packet, ids);
        return packet;
    }
}