package moe.plushie.armourers_workshop.client.palette;

import java.util.Arrays;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Palette {

    public static final int MAX_COLOURS_PER_PALETTE = 32;

    private String name;
    private final boolean locked;
    private int[] colours;

    public Palette(String name, boolean locked, int[] colours) {
        this.name = name;
        this.locked = locked;
        this.colours = colours;
    }

    public Palette(String name) {
        this(name, false, new int[MAX_COLOURS_PER_PALETTE]);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isLocked() {
        return locked;
    }

    public int[] getColours() {
        return colours;
    }

    public void setColour(int index, int colour) {
        colours[index] = colour;
    }

    public int getColour(int index) {
        return colours[index];
    }

    @Override
    public String toString() {
        return "Palette [name=" + name + ", colours=" + Arrays.toString(colours) + "]";
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
        return new Palette(name, locked, colours.clone());
    }
}
