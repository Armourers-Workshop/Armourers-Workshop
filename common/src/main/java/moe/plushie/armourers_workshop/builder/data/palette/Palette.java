package moe.plushie.armourers_workshop.builder.data.palette;

import com.apple.library.uikit.UIColor;

import java.util.Arrays;
import java.util.Objects;

public class Palette {

    public static final int MAX_COLORS_PER_PALETTE = 32;
    private final boolean locked;
    private String name;
    private final UIColor[] colors;

    public Palette(String name, boolean locked, UIColor[] colors) {
        this.name = name;
        this.locked = locked;
        this.colors = colors;
    }

    public Palette(String name) {
        this(name, false, new UIColor[MAX_COLORS_PER_PALETTE]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocked() {
        return locked;
    }

    public UIColor[] getColors() {
        return colors;
    }

    public void setColor(int index, UIColor color) {
        colors[index] = color;
    }

    public UIColor getColor(int index) {
        return colors[index];
    }

    @Override
    public String toString() {
        return "Palette [name=" + name + ", colors=" + Arrays.toString(colors) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Palette that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    protected Palette clone() {
        return new Palette(name, locked, colors.clone());
    }
}
