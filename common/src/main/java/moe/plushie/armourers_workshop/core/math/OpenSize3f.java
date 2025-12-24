package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.core.utils.Objects;

@SuppressWarnings("unused")
public class OpenSize3f {

    public static final OpenSize3f ZERO = new OpenSize3f(0, 0, 0);
    public static final OpenSize3f ONE = new OpenSize3f(1, 1, 1);

    public float width;
    public float height;
    public float depth;

    public OpenSize3f() {
    }

    public OpenSize3f(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }

    public float depth() {
        return depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenSize3f that)) return false;
        return Float.compare(width, that.width) == 0 && Float.compare(height, that.height) == 0 && Float.compare(depth, that.depth) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, depth);
    }

    @Override
    public String toString() {
        return OpenMath.format("(%f %f %f)", width, height, depth);
    }
}
