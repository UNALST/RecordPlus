package me.lst.recordplus.util.nms.v1_16_4;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtils {
    public static void send(Player player, Packet<?> packet) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection connection = nmsPlayer.playerConnection;
        connection.sendPacket(packet);
    }
}