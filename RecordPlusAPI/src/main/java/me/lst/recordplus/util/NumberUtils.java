package me.lst.recordplus.util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {
    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.US);
    private static final NumberFormat DECIMAL_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
        DECIMAL_FORMAT.setGroupingUsed(false);
    }

    public static String format(double d) {
        return DECIMAL_FORMAT.format(d);
    }

    public static String format(int i) {
        return FORMAT.format(i);
    }
}
