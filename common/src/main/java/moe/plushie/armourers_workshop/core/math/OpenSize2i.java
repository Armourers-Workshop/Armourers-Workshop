package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.ISize2i;
import moe.plushie.armourers_workshop.core.utils.Objects;

@SuppressWarnings("unused")
public class OpenSize2i implements ISize2i {

    public static final OpenSize2i ZERO = new OpenSize2i();

    public int width;
    public int height;

    public OpenSize2i() {
    }

    public OpenSize2i(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenSize2i that)) return false;
        return width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%d %d)", width, height);
    }
}
