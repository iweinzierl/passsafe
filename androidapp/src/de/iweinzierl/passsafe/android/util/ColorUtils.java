package de.iweinzierl.passsafe.android.util;

import android.graphics.Color;

public final class ColorUtils {

    private ColorUtils() { }

    public static int colorById(final int id) {
        int r = (150 - id * 25) % 255;
        int g = (200 + id * 75) % 255;
        int b = (125 + id * 125) % 255;

        return Color.rgb(r, g, b);
    }
}
