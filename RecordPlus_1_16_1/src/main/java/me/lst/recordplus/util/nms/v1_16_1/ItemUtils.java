package me.lst.recordplus.util.nms.v1_16_1;

import com.mojang.datafixers.util.Pair;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftMagicNumbers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ItemUtils {
    public static List<Pair<EnumItemSlot, ItemStack>> getItems(EntityLiving entity) {
        List<Pair<EnumItemSlot, ItemStack>> items = new ArrayList<>(EnumItemSlot.values().length);

        for (EnumItemSlot slot : EnumItemSlot.values()) {
            ItemStack item = entity.getEquipment(slot);
            items.add(Pair.of(slot, item));
        }
        return items;
    }

    public static String encodeItem(ItemStack item) {
        try {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInt("DataVersion", CraftMagicNumbers.INSTANCE.getDataVersion());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            NBTCompressedStreamTools.a(item.save(compound), outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not encode itemstack!", e);
        }
    }

    public static String itemsToString(List<Pair<EnumItemSlot, ItemStack>> items) {
        StringBuilder builder = new StringBuilder();

        boolean first = true;

        for (Pair<EnumItemSlot, ItemStack> item : items) {
            if (first) {
                first = false;
            } else {
                builder.append('\t');
            }
            builder.append(item.getFirst()).append('\0').append(encodeItem(item.getSecond()));
        }
        return builder.toString();
    }
}
