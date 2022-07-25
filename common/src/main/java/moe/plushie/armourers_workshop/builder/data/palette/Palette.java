package moe.plushie.armourers_workshop.builder.data.palette;

import java.util.Arrays;

public class Palette {

    public static final int MAX_COLOURS_PER_PALETTE = 32;
    private final boolean locked;
    private String name;
    private int[] colors;

    public Palette(String name, boolean locked, int[] colours) {
        this.name = name;
        this.locked = locked;
        this.colors = colours;
    }

    public Palette(String name) {
        this(name, false, new int[MAX_COLOURS_PER_PALETTE]);
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

    public int[] getColors() {
        return colors;
    }

    public void setColor(int index, int colour) {
        colors[index] = colour;
    }

    public int getColor(int index) {
        return colors[index];
    }

    @Override
    public String toString() {
        return "Palette [name=" + name + ", colours=" + Arrays.toString(colors) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Palette other = (Palette) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    protected Palette clone() {
        return new Palette(name, locked, colors.clone());
    }
}
