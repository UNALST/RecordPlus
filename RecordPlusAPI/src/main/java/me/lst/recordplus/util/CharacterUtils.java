package me.lst.recordplus.util;

public class CharacterUtils {
    public static boolean isInteger(char character) {
        return character >= '0' && character <= '9';
    }

    public static int getAsInteger(char character) {
        return character - '0';
    }
}