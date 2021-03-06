package me.lst.recordplus.nms.v1_16_3;

import java.util.HashMap;
import java.util.Map;

public class EntityMappings {
    // 1.16.2 - 1.16.4
    public static final Map<String, Integer> TYPE_IDS = new HashMap<>();
    
    static {
        TYPE_IDS.put("ARROW", 2);
        TYPE_IDS.put("BAT", 3);
        TYPE_IDS.put("BEE", 4);
        TYPE_IDS.put("BLAZE", 5);
        TYPE_IDS.put("BOAT", 6);
        TYPE_IDS.put("CAT", 7);
        TYPE_IDS.put("CAVE_SPIDER", 8);
        TYPE_IDS.put("CHICKEN", 9);
        TYPE_IDS.put("COD", 10);
        TYPE_IDS.put("COW", 11);
        TYPE_IDS.put("CREEPER", 12);
        TYPE_IDS.put("DOLPHIN", 13);
        TYPE_IDS.put("DONKEY", 14);
        TYPE_IDS.put("DROWNED", 16);
        TYPE_IDS.put("ELDER_GUARDIAN", 17);
        TYPE_IDS.put("ENDER_DRAGON", 19);
        TYPE_IDS.put("ENDERMAN", 20);
        TYPE_IDS.put("ENDERMITE", 21);
        TYPE_IDS.put("EVOKER_FANGS", 23);
        TYPE_IDS.put("FOX", 28);
        TYPE_IDS.put("GHAST", 29);
        TYPE_IDS.put("GIANT", 30);
        TYPE_IDS.put("GUARDIAN", 31);
        TYPE_IDS.put("HOGLIN", 32);
        TYPE_IDS.put("HORSE", 33);
        TYPE_IDS.put("HUSK", 34);
        TYPE_IDS.put("ILLUSIONER", 35);
        TYPE_IDS.put("IRON_GOLEM", 36);
        TYPE_IDS.put("LLAMA", 42);
        TYPE_IDS.put("LLAMA_SPIT", 43);
        TYPE_IDS.put("MAGMA_CUBE", 44);
        TYPE_IDS.put("MINECART", 45);
        TYPE_IDS.put("MULE", 52);
        TYPE_IDS.put("MUSHROOM_COW", 53);
        TYPE_IDS.put("OCELOT", 54);
        TYPE_IDS.put("PANDA", 56);
        TYPE_IDS.put("PARROT", 57);
        TYPE_IDS.put("PHANTOM", 58);
        TYPE_IDS.put("PIG", 59);
        TYPE_IDS.put("PIGLIN", 60);
        TYPE_IDS.put("PIGLIN_BRUTE", 61);
        TYPE_IDS.put("PILLAGER", 62);
        TYPE_IDS.put("POLAR_BEAR", 63);
        TYPE_IDS.put("PUFFERFISH", 65);
        TYPE_IDS.put("RABBIT", 66);
        TYPE_IDS.put("RAVAGER", 67);
        TYPE_IDS.put("SALMON", 68);
        TYPE_IDS.put("SHEEP", 69);
        TYPE_IDS.put("SHULKER", 70);
        TYPE_IDS.put("SILVERFISH", 72);
        TYPE_IDS.put("SKELETON", 73);
        TYPE_IDS.put("SKELETON_HORSE", 74);
        TYPE_IDS.put("SLIME", 75);
        TYPE_IDS.put("SNOW_GOLEM", 77);
        TYPE_IDS.put("SNOWBALL", 78);
        TYPE_IDS.put("SPIDER", 80);
        TYPE_IDS.put("SQUID", 81);
        TYPE_IDS.put("STRAY", 82);
        TYPE_IDS.put("STRIDER", 83);
        TYPE_IDS.put("ENDER_PEARL", 85);
        TYPE_IDS.put("SPLASH_POTION", 87);
        TYPE_IDS.put("TRADER_LLAMA", 89);
        TYPE_IDS.put("TROPICAL_FISH", 90);
        TYPE_IDS.put("TURTLE", 91);
        TYPE_IDS.put("VEX", 92);
        TYPE_IDS.put("VILLAGER", 93);
        TYPE_IDS.put("VINDICATOR", 94);
        TYPE_IDS.put("WANDERING_TRADER", 95);
        TYPE_IDS.put("WITCH", 96);
        TYPE_IDS.put("WITHER", 97);
        TYPE_IDS.put("WITHER_SKELETON", 98);
        TYPE_IDS.put("WOLF", 100);
        TYPE_IDS.put("ZOGLIN", 101);
        TYPE_IDS.put("ZOMBIE", 102);
        TYPE_IDS.put("ZOMBIE_HORSE", 103);
        TYPE_IDS.put("ZOMBIE_VILLAGER", 104);
        TYPE_IDS.put("ZOMBIFIED_PIGLIN", 105);
    }

    public static Integer getTypeId(String type) {
        return TYPE_IDS.get(type);
    }
}
