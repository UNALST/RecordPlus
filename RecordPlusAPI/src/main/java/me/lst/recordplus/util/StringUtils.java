package me.lst.recordplus.util;

import org.bukkit.ChatColor;

import java.util.UUID;

public class StringUtils {
    public static String format(String string, Object... args) {
        if (args.length != 0) {
            string = String.format(string, args);
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String asString(String deliminator, Object[] objects) {
        StringBuilder builder = new StringBuilder();

        boolean first = true;

        for (Object object : objects) {
            if (first) {
                first = false;
            } else {
                builder.append(deliminator);
            }
            builder.append(object);
        }
        return builder.toString();
    }

    public static String asString(Object... objects) {
        return asString(" ", objects);
    }

    public static String random(int characters) {
        if (characters <= 0) {
            throw new IndexOutOfBoundsException("Minimum character length is 0!");
        }
        if (characters > 32) {
            throw new IndexOutOfBoundsException("Character length cannot exceed 32!");
        }
        return UUID.randomUUID().toString().replace("-", "").substring(0, characters);
    }
}
