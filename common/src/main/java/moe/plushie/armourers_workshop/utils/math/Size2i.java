package moe.plushie.armourers_workshop.utils.math;

import moe.plushie.armourers_workshop.api.math.ISize2i;

import java.util.Objects;

@SuppressWarnings("unused")
public class Size2i implements ISize2i {

    public int width;
    public int height;

    public Size2i(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size2i that)) return false;
        return width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return String.format("(%d %d)", width, height);
    }
}
