package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.core.utils.Objects;

@SuppressWarnings("unused")
public class OpenSize2f {

    public static final OpenSize2f ZERO = new OpenSize2f();

    public float width;
    public float height;

    public OpenSize2f() {
    }

    public OpenSize2f(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public OpenSize2f(double width, double height) {
        this((float) width, (float) height);
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenSize2f that)) return false;
        return Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%f %f)", width, height);
    }
}
