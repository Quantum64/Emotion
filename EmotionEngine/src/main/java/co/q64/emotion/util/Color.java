package co.q64.emotion.util;

import com.google.auto.factory.AutoFactory;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Color {
    private @Getter final int r, g, b;

    private Color(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    private Color(String value) {
        try {
            int color = Integer.decode(value);
            this.r = (color >> 16) & 0xFF;
            this.g = (color >> 8) & 0xFF;
            this.b = (color) & 0xFF;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(value + " does not appear to be a valid color code.");
        }
    }

    public static Color of(String value) {
        return new Color(value);
    }

    public static Color of(int r, int g, int b) {
        return new Color(r, g, b);
    }
}